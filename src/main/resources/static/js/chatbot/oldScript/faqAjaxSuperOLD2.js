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
                // Utente clicca una delle scelte
                addOutgoingMessage(choice.text);
                if (choice.dataId === "genere-libri") {
                    caricaDeterminaGenere();
                } else if (choice.dataId === "info-sito") {
                    caricaInfoSito();
                }
                // Rimuove i bottoni iniziali dopo il click
                li.remove();
            });

            p.appendChild(button);
        });

        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        // Se vogliamo salvare la fase "CHOICE"
        if (sendPhase) {
            storeMessageInSession("fase", "CHOICE", null, null, "CHOICE");
        }
    }

    // -----------------------------
    // 3) Caricare Domande
    // -----------------------------
    function caricaDomandeAppend() {
        $.ajax({
            url: "/faq/domande",
            method: "GET",
            success: function(html) {
                // Inseriamo l'HTML (fragment Thymeleaf) con i bottoni delle domande
                chatbox.insertAdjacentHTML("beforeend", html);
                attachDomandaClickEvents();
                chatbox.scrollTop = chatbox.scrollHeight;

                // Aggiorna la fase
                storeMessageInSession("fase", "DISPLAY_DOMANDE", null, null, "DISPLAY_DOMANDE");
            },
            error: function() {
                addIncomingMessage("Errore nel caricamento delle domande.");
            }
        });
    }

    // Quando clicco una domanda
    function attachDomandaClickEvents() {
        const bottoniDomanda = document.querySelectorAll(".domanda-button");
        bottoniDomanda.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const domandaIdStr = bottone.getAttribute("data-id");
                const domandaId = parseInt(domandaIdStr, 10);
                const testoDomanda = bottone.textContent || "Domanda selezionata";
                const linkDomanda = bottone.getAttribute("data-link");

                addOutgoingMessage(testoDomanda);

                if(linkDomanda) {
                    window.location.href = linkDomanda;
                }
                // Carico le risposte
                $.ajax({
                    url: `/faq/${domandaId}/risposte`,
                    method: "GET",
                    success: function(html) {
                        chatbox.insertAdjacentHTML("beforeend", html);
                        attachRispostaClickEvents();
                        chatbox.scrollTop = chatbox.scrollHeight;

                        // Fase = DISPLAY_RISPOSTE
                        storeMessageInSession("fase", "DISPLAY_RISPOSTE", domandaId, null, "DISPLAY_RISPOSTE");
                    },
                    error: function() {
                        addIncomingMessage("Errore nel caricamento delle risposte.");
                    }
                });
            });
        });
    }

    function addAssistenzaButton() {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
        <span class="material-symbols-outlined">smart_toy</span>
        <div>
            <p>
                <a href="mailto:assistenza@biblionet.it" class="assistenza-button">
                    Se la risposta non ti ha soddisfatto,
                    Contatta Assistenza
                </a>
            </p>
        </div>
    `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
    }

    // Quando clicco una risposta
    function attachRispostaClickEvents() {
        const bottoniRisposta = document.querySelectorAll(".risposta-button");
        bottoniRisposta.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const rispostaIdStr = bottone.getAttribute("data-idRisposta");
                const rispostaId = parseInt(rispostaIdStr, 10);
                const testoRisposta = bottone.textContent || "Risposta selezionata";

                addOutgoingMessage(testoRisposta);
                bottone.disabled = true; // Opzionale, per disabilitare il bottone

                // Fase = ANSWER_SELECTED
                storeMessageInSession("fase", "ANSWER_SELECTED", null, rispostaId, "ANSWER_SELECTED");


            });
        });
    }

    // -----------------------------
    // Info sito + Determina genere
    // -----------------------------
    function caricaInfoSito() {
        addIncomingMessage("Ecco le info sul sito: ....");
        // Cambiamo la fase e carichiamo le domande
        storeMessageInSession("fase", null, null, null, "DISPLAY_DOMANDE");
        caricaDomandeAppend();
    }

    function caricaDeterminaGenere() {
        // Mostra un messaggio iniziale
        addIncomingMessage("Carico il questionario per determinare il tuo genere preferito...");

        // Effettua una richiesta AJAX al controller per ottenere il questionario
        $.ajax({
            url: "/faq/questionario/list", // URL del controller per caricare il questionario
            method: "GET",
            success: function(html) {
                // Inserisce il fragment del questionario nella chatbox
                chatbox.insertAdjacentHTML("beforeend", html);

                // Aggiunge eventi ai bottoni del questionario
                attachQuestionarioClickEvents();

                chatbox.scrollTop = chatbox.scrollHeight;

                // Salva la fase nella sessione
                storeMessageInSession("fase", "QUESTIONARIO", null, null, "QUESTIONARIO");
            },
            error: function() {
                // Messaggio di errore in caso di problemi con il caricamento
                addIncomingMessage("Errore durante il caricamento del questionario. Riprova più tardi.");
            }
        });
    }

    // -----------------------------
    // 4) Gestione Sessione Spring (Ajax)
    // -----------------------------
    function storeMessageInSession(type, text, domandaId = null, rispostaId = null, fase = null) {
        let data = { type, text };
        if (domandaId) data.domandaId = domandaId;
        if (rispostaId) data.rispostaId = rispostaId;
        if (fase) data.fase = fase;

        console.log("Dati inviati alla sessione:", JSON.stringify(data));

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

    // -----------------------------
    // 5) loadSessionMessages: recupera TUTTO, poi chiama handlePhase
    // -----------------------------
    function loadSessionMessages() {
        $.ajax({
            url: "/faq/getSessionBot",
            method: "GET",
            dataType: "json",
            success: function (sessionData) {
                const {
                    messages,
                    faseCorrente,
                    domande,
                    risposte,
                    utenteValido
                } = sessionData;

                // Se l'utente non è valido, reimposta la sessione
                if (!utenteValido) {
                    console.warn("Utente non valido, reimpostazione della sessione.");
                    chatbox.innerHTML = ""; // Pulisci la chat
                    addIncomingMessage("Ciao, come posso aiutarti?");
                    addChoiceButtons([
                        { text: "Determina genere libri", dataId: "genere-libri" },
                        { text: "Info sul sito", dataId: "info-sito" }
                    ]);
                    return;
                }
                chatbox.innerHTML = "";

                messages.forEach(msg => {
                    if (msg.type === "incoming" || msg.type === "outgoing") {
                        const li = document.createElement("li");
                        li.classList.add("chat", msg.type);
                        li.innerHTML = `<div><p>${msg.text}</p></div>`;
                        chatbox.appendChild(li);
                    }
                });

                if (faseCorrente) {
                    handlePhase(faseCorrente, sessionData.ultimaDomanda, sessionData.ultimaRisposta, domande, risposte);
                }

                chatbox.scrollTop = chatbox.scrollHeight;
            },
            error: function () {
                console.error("Errore nel recupero dello stato della chat");
            }
        });
    }



    // -----------------------------
    // 6) handlePhase: ricrea i bottoni e la UI
    // -----------------------------
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
                // Se il backend ti ha mandato le domande
                if (domande && domande.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span><div><p>Seleziona una domanda:</p></div>`;
                    const p = li.querySelector("p");

                    domande.forEach((domanda) => {
                        const button = document.createElement("button");
                        button.classList.add("domanda-button");
                        // Attenzione: se l'entità si chiama `idDomanda`, rispetta i nomi
                        button.setAttribute("data-id", domanda.idDomanda);
                        button.textContent = domanda.contenuto;
                        button.setAttribute("data-link", domanda.mapLink);

                        p.appendChild(button);
                    });

                    chatbox.appendChild(li);
                    attachDomandaClickEvents();
                    chatbox.scrollTop = chatbox.scrollHeight;
                }
                break;

            case "DISPLAY_RISPOSTE":
                // Se il backend ti ha mandato le risposte
                const domandaSelezionata = domande?.find(domanda => domanda.idDomanda === domandaId);

                if (domandaSelezionata) {
                    // Aggiungi il messaggio outgoing per la domanda selezionata
                    addOutgoingMessage(domandaSelezionata.contenuto);
                } else {
                    console.warn("Domanda selezionata non trovata o non disponibile.");
                }


                if (risposte && risposte.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span><div><p>Seleziona una risposta:</p></div>`;
                    const p = li.querySelector("p");

                    risposte.forEach((r) => {
                        const button = document.createElement("button");
                        button.classList.add("risposta-button");
                        button.setAttribute("data-idRisposta", r.idRisposta);
                        button.textContent = r.contenuto;
                        p.appendChild(button);
                    });



                    chatbox.appendChild(li);
                    attachRispostaClickEvents();
                    addAssistenzaButton();
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
    // 7) Apertura/Reset Chat con Gestione della Sessione
    // -----------------------------
    chatbotToggler.addEventListener("click", () => {
        // Se la chat è chiusa, la apriamo
        if (!document.body.classList.contains("show-chatbot")) {
            if (!chatInizializzata) {
                // Carichiamo lo stato dalla sessione solo la prima volta
                loadSessionMessages();
            }
            document.body.classList.add("show-chatbot");
        }
        // Altrimenti la resettiamo
        else {
            chatbox.innerHTML = "";
            chatInizializzata = false;

            $.ajax({
                url: "/faq/clearSession",
                method: "POST",
                success: function() {
                    console.log("Sessione della chat resettata");
                    // Stato iniziale
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
    // 8) Chiusura con la X in alto
    // -----------------------------
    headerClose?.addEventListener("click", () => {
        document.body.classList.remove("show-chatbot");
    });

    // -----------------------------
    // 9) Carica lo stato della chat all'avvio
    // -----------------------------
    loadSessionMessages();
});