document.addEventListener("DOMContentLoaded", () => {
    const chatbotToggler = document.querySelector("#chatbot-toggler"); // Pulsante in basso
    const headerClose = document.querySelector("#header-close");       // X in alto
    const chatbox = document.querySelector("#chatbox");               // ul.chatbox

    let chatInizializzata = false;

    // -----------------------------
    // 1) Messaggi base con Salvataggio in Sessione
    // -----------------------------
    // Messaggio del bot (incoming)
    function addIncomingMessage(testo) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
            <span class="material-symbols-outlined">smart_toy</span>
            <div>
                <p>${testo}</p>
            </div>
        `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        // Salva il messaggio nella sessione senza aggiornare lo stato
        storeMessageInSession("incoming", testo, null, null, null);
    }

    // Messaggio dell'utente (outgoing)
    function addOutgoingMessage(testo) {
        const li = document.createElement("li");
        li.classList.add("chat", "outgoing");
        li.innerHTML = `<div><p>${testo}</p></div>`;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        // Salva il messaggio nella sessione senza aggiornare lo stato
        storeMessageInSession("outgoing", testo, null, null, null);
    }

    // -----------------------------
    // 2) Scelta Iniziale (opzioni)
    // -----------------------------
    function addChoiceButtons(choices, sendPhase = true) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span><div><p></p></div>`;

        const p = li.querySelector("p");

        choices.forEach(choice => {
            const button = document.createElement("button");
            button.classList.add("scelta-button", "chatbot-button");
            button.setAttribute("data-id", choice.dataId);
            button.textContent = choice.text;

            button.addEventListener("click", () => {
                addOutgoingMessage(choice.text);
                if (choice.dataId === "genere-libri") {
                    caricaDeterminaGenere();
                } else if (choice.dataId === "info-sito") {
                    caricaInfoSito();
                }

                // Rimuove i bottoni iniziali
                li.remove();
            });

            p.appendChild(button);
        });

        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        if (sendPhase) {
            // Salva la fase corrente nella sessione solo se sendPhase è true
            storeMessageInSession("fase", "CHOICE", null, null, "CHOICE");
        }
    }

    // -----------------------------
    // 3) Caricare Domande da /faq/domande (append)
    // -----------------------------
    function caricaDomandeAppend() {
        $.ajax({
            url: "/faq/domande",
            method: "GET",
            success: function(html) {
                chatbox.insertAdjacentHTML("beforeend", html);
                attachDomandaClickEvents();
                chatbox.scrollTop = chatbox.scrollHeight;

                // Aggiorna la fase corrente
                storeMessageInSession("fase", "DISPLAY_DOMANDE", null, null, "DISPLAY_DOMANDE");
            },
            error: function() {
                addIncomingMessage("Errore nel caricamento delle domande.");
            }
        });
    }

    // Quando l'utente clicca su un .domanda-button
    function attachDomandaClickEvents() {
        const bottoniDomanda = document.querySelectorAll(".domanda-button");
        bottoniDomanda.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const domandaIdStr = bottone.getAttribute("data-id");
                const domandaId = parseInt(domandaIdStr, 10);
                const testoDomanda = bottone.textContent || "Domanda selezionata";
                addOutgoingMessage(testoDomanda);

                $.ajax({
                    url: `/faq/${domandaId}/risposte`,
                    method: "GET",
                    success: function(html) {
                        chatbox.insertAdjacentHTML("beforeend", html);
                        attachRispostaClickEvents(); // Aggiungi questa funzione per gestire le risposte
                        chatbox.scrollTop = chatbox.scrollHeight;

                        // Aggiorna la fase corrente
                        storeMessageInSession("fase", "DISPLAY_RISPOSTE", domandaId, null, "DISPLAY_RISPOSTE");
                    },
                    error: function() {
                        addIncomingMessage("Errore nel caricamento delle risposte.");
                    }
                });
            });
        });
    }

    // Quando l'utente clicca su un .risposta-button
    function attachRispostaClickEvents() {
        const bottoniRisposta = document.querySelectorAll(".risposta-button");
        bottoniRisposta.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const rispostaIdStr = bottone.getAttribute("data-idRisposta");
                console.log("Hai cliccato una risposta, data-idRisposta:", rispostaIdStr);

                const rispostaId = parseInt(rispostaIdStr, 10);
                const testoRisposta = bottone.textContent || "Risposta selezionata";

                addOutgoingMessage(testoRisposta);

                // Aggiorna fase con rispostaId
                storeMessageInSession("fase", "ANSWER_SELECTED", null, rispostaId, "ANSWER_SELECTED");
            });
        });
    }

    // “Info sul sito”: messaggio + carica domande
    function caricaInfoSito() {
        addIncomingMessage("Ecco le info sul sito: ....");
        storeMessageInSession("fase", null, null, null, "DISPLAY_DOMANDE");
        caricaDomandeAppend();
    }
    // “Determina genere libri”
    function caricaDeterminaGenere() {
        addIncomingMessage("Da implementare...");
    }

    // -----------------------------
    // 4) Gestione Sessione Spring tramite AJAX
    // -----------------------------
    // Salva un messaggio o uno stato in ChatSession via POST a /faq/sessionChat
    function storeMessageInSession(type, text, domandaId = null, rispostaId = null, fase = null) {
        let data = { type, text };

        if (domandaId) {
            data.domandaId = domandaId;
        }
        if (rispostaId) {
            data.rispostaId = rispostaId;
        }
        if (fase) {
            data.fase = fase;
        }

        console.log("Dati inviati alla sessione:", JSON.stringify(data)); // <--- log JSON

        $.ajax({
            url: "/faq/sessionChat",
            method: "POST",
            data: data,
            success: function () {
                console.log("Messaggio salvato nella sessione", data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Impossibile salvare il messaggio in sessione:", textStatus, errorThrown);
            },
        });
    }

    // Carica i messaggi esistenti e lo stato da ChatSession via GET a /faq/getSessionBot
    function loadSessionMessages() {
        $.ajax({
            url: "/faq/getSessionBot",
            method: "GET",
            dataType: "json",
            success: function (sessionData) {
                const { messages, faseCorrente, domande } = sessionData;

                // Ripulisci la chatbox
                chatbox.innerHTML = "";

                // Aggiungi i messaggi visibili
                messages.forEach((msg) => {
                    if (msg.type === "incoming" || msg.type === "outgoing") {
                        const li = document.createElement("li");
                        li.classList.add("chat", msg.type);
                        li.innerHTML = `<div><p>${msg.text}</p></div>`;
                        chatbox.appendChild(li);
                    }
                });

                // Ripristina lo stato in base alla fase corrente
                if (faseCorrente === "DISPLAY_DOMANDE" && domande && domande.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `<div><p>Seleziona una domanda:</p></div>`;
                    const p = li.querySelector("p");

                    domande.forEach((domanda) => {
                        const button = document.createElement("button");
                        button.classList.add("domanda-button");
                        button.setAttribute("data-id", domanda.id);
                        button.textContent = domanda.testo;
                        p.appendChild(button);
                    });

                    chatbox.appendChild(li);
                    attachDomandaClickEvents();
                    chatbox.scrollTop = chatbox.scrollHeight;
                }
            },
            error: function () {
                console.error("Errore nel recupero dello stato della chat");
            },
        });
    }

    // Gestisce la fase corrente per ricostruire l'interfaccia utente
    function handlePhase(phase, domandaId, rispostaId, domande, risposte) {
        console.log("handlePhase:", phase, domandaId, rispostaId, domande, risposte);

        switch (phase) {
            case "CHOICE":
                addChoiceButtons(
                    [
                        { text: "Determina genere libri", dataId: "genere-libri" },
                        { text: "Info sul sito", dataId: "info-sito" },
                    ],
                    false
                );
                break;

            case "DISPLAY_DOMANDE":
                if (domande && domande.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span><div><p>Seleziona una domanda:</p></div>`;
                    const p = li.querySelector("p");

                    domande.forEach(domanda => {
                        const button = document.createElement("button");
                        button.classList.add("domanda-button");
                        button.setAttribute("data-id", domanda.idDomanda);
                        button.textContent = domanda.contenuto;
                        p.appendChild(button);
                    });

                    chatbox.appendChild(li);
                    attachDomandaClickEvents();
                    chatbox.scrollTop = chatbox.scrollHeight;
                }
                break;

            case "DISPLAY_RISPOSTE":
                if (risposte && risposte.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span><div><p>Seleziona una risposta:</p></div>`;
                    const p = li.querySelector("p");

                    risposte.forEach(r => {
                        const button = document.createElement("button");
                        button.classList.add("risposta-button");
                        button.setAttribute("data-idRisposta", r.idRisposta);
                        button.textContent = r.contenuto;
                        p.appendChild(button);
                    });

                    chatbox.appendChild(li);
                    attachRispostaClickEvents();
                    chatbox.scrollTop = chatbox.scrollHeight;
                }
                break;

            case "ANSWER_SELECTED":
                addIncomingMessage("Hai selezionato una risposta.");
                break;

            default:
                console.warn("Fase non riconosciuta:", phase);
        }
    }

    // -----------------------------
    // 5) Apertura/Reset Chat con Gestione della Sessione
    // -----------------------------
    chatbotToggler.addEventListener("click", () => {
        // Se la chat è CHIUSA, la apriamo
        if (!document.body.classList.contains("show-chatbot")) {
            if (!chatInizializzata) {
                // Inizializziamo solo la prima volta caricando lo stato dalla sessione
                loadSessionMessages();
            }
            document.body.classList.add("show-chatbot");
        }
        // Se la chat è APERTA, la resettiamo
        else {
            // Svuotiamo la chat localmente
            chatbox.innerHTML = "";
            chatInizializzata = false;

            // Reset della sessione tramite endpoint /faq/clearSession
            $.ajax({
                url: "/faq/clearSession",
                method: "POST",
                success: function() {
                    console.log("Sessione della chat resettata");
                    // Reimposta lo stato iniziale
                    addIncomingMessage("Ciao, come posso aiutarti?");
                    addChoiceButtons([
                        { text: "Determina genere libri", dataId: "genere-libri" },
                        { text: "Info sul sito", dataId: "info-sito" }
                    ]);
                },
                error: function() {
                    console.error("Impossibile resettare la sessione della chat");
                    addIncomingMessage("Errore nel resettare la sessione della chat.");
                }
            });
        }
    });

    // -----------------------------
    // 6) Chiusura con la X in alto
    // -----------------------------
    headerClose?.addEventListener("click", () => {
        document.body.classList.remove("show-chatbot");
    });

    // -----------------------------
    // 7) Carica lo stato della chat all'avvio
    // -----------------------------
    loadSessionMessages();
});
