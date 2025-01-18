document.addEventListener("DOMContentLoaded", () => {
    const chatbotToggler = document.querySelector("#chatbot-toggler"); // Pulsante (icona) in basso
    const headerClose = document.querySelector("#header-close");       // X in alto
    const chatbox = document.querySelector("#chatbox");               // ul.chatbox

    let chatInizializzata = false;
    let indiceDomanda = 0;

    // =========================================================
    // (1) Carichiamo sempre i messaggi all'avvio
    // =========================================================
    loadSessionMessages();

    // =========================================================
    // (2) Se l'utente aveva lasciato il chatbot aperto, lo riapro
    // =========================================================
    const wasChatOpen = localStorage.getItem("chatOpen") === "true";
    if (wasChatOpen) {
        document.body.classList.add("show-chatbot");
    }

    // -----------------------------
    // 1) Messaggi base (incoming/outgoing)
    // -----------------------------
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

        storeMessageInSession("incoming", testo, null, null, null);
    }

    function addOutgoingMessage(testo) {
        const li = document.createElement("li");
        li.classList.add("chat", "outgoing");
        li.innerHTML = `<div><p>${testo}</p></div>`;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

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
                li.remove();
            });

            p.appendChild(button);
        });

        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        if (sendPhase) {
            storeMessageInSession("fase", "CHOICE", null, null, "CHOICE");
        }
    }

    // -----------------------------
    // 3) Caricare Domande (FAQ)
    // -----------------------------
    function caricaDomandeAppend() {
        $.ajax({
            url: "/faq/domande",
            method: "GET",
            success: function(html) {
                chatbox.insertAdjacentHTML("beforeend", html);
                attachDomandaClickEvents();
                chatbox.scrollTop = chatbox.scrollHeight;

                storeMessageInSession("fase", "DISPLAY_DOMANDE", null, null, "DISPLAY_DOMANDE");
            },
            error: function() {
                addIncomingMessage("Errore nel caricamento delle domande.");
            }
        });
    }

    function attachDomandaClickEvents() {
        const bottoniDomanda = document.querySelectorAll(".domanda-button");
        bottoniDomanda.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const domandaIdStr = bottone.getAttribute("data-id");
                const domandaId = parseInt(domandaIdStr, 10);
                const testoDomanda = bottone.textContent || "Domanda selezionata";
                const linkDomanda = bottone.getAttribute("data-link");

                addOutgoingMessage(testoDomanda);

                if (linkDomanda) {
                    localStorage.setItem("chatOpen", "true");
                    window.location.href = linkDomanda;
                }

                $.ajax({
                    url: `/faq/${domandaId}/risposte`,
                    method: "GET",
                    success: function(html) {
                        chatbox.insertAdjacentHTML("beforeend", html);
                        attachRispostaClickEvents();
                        chatbox.scrollTop = chatbox.scrollHeight;

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

    function attachRispostaClickEvents() {
        const bottoniRisposta = document.querySelectorAll(".risposta-button");
        bottoniRisposta.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const rispostaIdStr = bottone.getAttribute("data-idRisposta");
                const rispostaId = parseInt(rispostaIdStr, 10);
                const testoRisposta = bottone.textContent || "Risposta selezionata";

                addOutgoingMessage(testoRisposta);
                bottone.disabled = true;

                storeMessageInSession("fase", "ANSWER_SELECTED", null, rispostaId, "ANSWER_SELECTED");
            });
        });
    }

    // -----------------------------
    // 4) Info sito + Determina genere
    // -----------------------------
    function caricaInfoSito() {
        addIncomingMessage("Ecco le info sul sito: ....");
        storeMessageInSession("fase", null, null, null, "DISPLAY_DOMANDE");
        caricaDomandeAppend();
    }

    function inviaRisposteSelezionate() {
        // Raccogli tutti gli ID delle risposte selezionate
        const risposteSelezionate = [...document.querySelectorAll(".questionario-button.selected")]
            .map(risposta => parseInt(risposta.dataset.id, 10)); // Ottieni gli ID come numeri

        // Verifica che ci siano risposte selezionate
        if (risposteSelezionate.length === 0) {
            alert("Seleziona almeno una risposta prima di procedere!");
            return;
        }

        // Invia le risposte selezionate al backend con AJAX
        $.ajax({
            url: '/faq/calcolaGenere',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(risposteSelezionate), // Converti gli ID in JSON
            success: function (data) {
                // Mostra il genere predominante
                alert(data);
            },
            error: function (xhr, status, error) {
                console.error("Errore:", error);
                alert("Si è verificato un errore durante il calcolo del genere predominante.");
            }
        });
    }


    function caricaDeterminaGenere() {
        addIncomingMessage("Carico il questionario per determinare il tuo genere preferito...");

        $.ajax({
            url: "/faq/questionario/inizia",
            method: "GET",
            success: function(data) {
                if (data.questionarioCompletato) {
                    addIncomingMessage("Il questionario è già completato o non ci sono domande disponibili.");
                } else {
                    mostraProssimaDomanda(data.prossimaDomanda, data.indiceDomanda);
                }
            },
            error: function() {
                addIncomingMessage("Errore durante il caricamento del questionario. Riprova più tardi.");
            }
        });
    }

    function mostraProssimaDomanda(domanda, indiceDomanda) {
        // Creazione del contenitore per la domanda e le risposte
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
         <span class="material-symbols-outlined">smart_toy</span>
          <div>
           <p>${domanda.contenuto}</p>
            <div id="risposte-container" class="chat incoming">
             ${domanda.risposte.map(risposta => `
                <button class="questionario-button" data-id="${risposta.idRisposta}">
                    ${risposta.contenuto}
                </button>
            `).join("")}
        </div>
    </div>
`;


        // Aggiunta della domanda alla chatbox
        chatbox.appendChild(li);

        // Controllo del pulsante "Invia"
        let inviaRispostaButton = document.querySelector("#invia-risposta");
        if (!inviaRispostaButton) {
            inviaRispostaButton = document.createElement("button");
            inviaRispostaButton.id = "invia-risposta";
            inviaRispostaButton.disabled = true;
            inviaRispostaButton.textContent = "Invia";
            inviaRispostaButton.classList.add("chat", "incoming", "risposta-button");
            chatbox.appendChild(inviaRispostaButton);
        } else {
            inviaRispostaButton.removeEventListener("click", inviaRispostaHandler);
            chatbox.appendChild(inviaRispostaButton);
        }


        // Scroll automatico alla fine della chat
        chatbox.scrollTop = chatbox.scrollHeight;

        // Associazione degli eventi ai pulsanti delle risposte
        attachQuestionarioClickEvents(indiceDomanda, inviaRispostaButton);
    }



    function attachQuestionarioClickEvents(indiceDomanda, inviaRispostaButton) {
        const bottoniQuestionario = document.querySelectorAll(".questionario-button");

        let rispostaSelezionata = null;

        bottoniQuestionario.forEach(bottone => {
            bottone.addEventListener("click", () => {
                bottoniQuestionario.forEach(btn => btn.classList.remove("selected"));
                bottone.classList.add("selected");
                rispostaSelezionata = parseInt(bottone.dataset.id, 10);
                inviaRispostaButton.disabled = false;
            });
        });

        inviaRispostaButton.addEventListener("click", () => {
            if (rispostaSelezionata) {
                $.ajax({
                    url: `/faq/nextDomanda/${indiceDomanda}`,
                    method: "GET",
                    data: {
                        indiceDomanda: indiceDomanda,
                        rispostaId: rispostaSelezionata
                    },
                    success: function(data) {
                        if (data.questionarioCompletato) {
                            addIncomingMessage("Grazie! Hai completato il questionario.");
                            mostraCalcolaGenereButton();
                        } else {
                            mostraProssimaDomanda(data.prossimaDomanda, indiceDomanda + 1);
                        }
                    },
                    error: function() {
                        addIncomingMessage("Errore durante il caricamento della prossima domanda.");
                    }
                });
            }
        });
    }


    function mostraCalcolaGenereButton() {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
    <span class="material-symbols-outlined">smart_toy</span>
    <div>
        <button id="calcola-genere-button" class="invia-button">Calcola Genere</button>
    </div>
    `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        document.querySelector("#calcola-genere-button").addEventListener("click", inviaRisposteSelezionate);
    }


    // -----------------------------
    // Gestione Sessione Spring (Ajax)
    // -----------------------------
    function storeMessageInSession(type, text, domandaId = null, rispostaId = null, fase = null) {
        let data = { type, text };
        if (domandaId) data.domandaId = domandaId;
        if (rispostaId) data.rispostaId = rispostaId;
        if (fase) data.fase = fase;

        $.ajax({
            url: "/faq/sessionChat",
            method: "POST",
            data: data,
            success: function () {
                console.log("Messaggio salvato in sessione:", data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Impossibile salvare il messaggio in sessione:", textStatus, errorThrown);
            },
        });
    }

    // -----------------------------
    // Caricamento Messaggi dalla Sessione
    // -----------------------------
    function loadSessionMessages() {
        $.ajax({
            url: "/faq/getSessionBot",
            method: "GET",
            dataType: "json",
            success: function (sessionData) {
                console.log("Session Data:", sessionData);
                const {
                    messages,
                    faseCorrente,
                    domande,
                    risposte,
                    utenteValido,
                    ultimaDomanda,
                    ultimaRisposta
                } = sessionData;

                chatbox.innerHTML = "";

                // Se l'utente non è valido, si riparte da zero
                if (!utenteValido) {
                    console.warn("Utente non valido, reimpostazione della sessione.");
                    addIncomingMessage("Ciao, come posso aiutarti?");
                    addChoiceButtons([
                        { text: "Vuoi che ti aiuti a scegliere il genere Più adatto a te?", dataId: "genere-libri" },
                        { text: "Vuoi che ti aiuti sulle funzionalità del sito?", dataId: "info-sito" }
                    ]);
                    return;
                }

                // Ricrea i messaggi passati
                if (messages && messages.length > 0) {
                    messages.forEach(msg => {
                        if (msg.type === "incoming" || msg.type === "outgoing") {
                            const li = document.createElement("li");
                            li.classList.add("chat", msg.type);
                            li.innerHTML = `<div><p>${msg.text}</p></div>`;
                            chatbox.appendChild(li);
                        }
                    });
                } else {
                    // Se non ci sono messaggi, allora siamo all'avvio
                    addIncomingMessage("Ciao, come posso aiutarti?");
                    addChoiceButtons([
                        { text: "Vuoi che ti aiuti a scegliere il genere Più adatto a te?", dataId: "genere-libri" },
                        { text: "Vuoi che ti aiuti sulle funzionalità del sito?", dataId: "info-sito" }
                    ]);
                }

                // Se la fase è presente, ricostruisci l'interfaccia
                if (faseCorrente) {
                    handlePhase(faseCorrente, ultimaDomanda, ultimaRisposta, domande, risposte);
                }

                chatbox.scrollTop = chatbox.scrollHeight;
                chatInizializzata = true;
            },
            error: function () {
                console.error("Errore nel recupero dello stato della chat");
            }
        });
    }

    // -----------------------------
    // Ricostruisci la UI in base alla fase
    // -----------------------------
    function handlePhase(phase, domandaId, rispostaId, domande, risposte) {
        console.log("handlePhase:", phase, domandaId, rispostaId, domande, risposte);
        switch (phase) {
            case "CHOICE":
                addChoiceButtons(
                    [
                        { text: "Vuoi che ti aiuti a scegliere il genere Più adatto a te?", dataId: "genere-libri" },
                        { text: "Vuoi che ti aiuti sulle funzionalità del sito?", dataId: "info-sito" },
                    ],
                    false
                );
                break;

            case "DISPLAY_DOMANDE":
                if (domande && domande.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `
                        <span class="material-symbols-outlined">smart_toy</span>
                        <div><p>Seleziona una domanda:</p></div>
                    `;
                    const p = li.querySelector("p");

                    domande.forEach((domanda) => {
                        const button = document.createElement("button");
                        button.classList.add("domanda-button");
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
                const domandaSelezionata = domande?.find(d => d.idDomanda === domandaId);
                if (domandaSelezionata) {
                    addOutgoingMessage(domandaSelezionata.contenuto);
                }

                if (risposte && risposte.length > 0) {
                    const li = document.createElement("li");
                    li.classList.add("chat", "incoming");
                    li.innerHTML = `
                        <span class="material-symbols-outlined">smart_toy</span>
                        <div><p>Seleziona una risposta:</p></div>
                    `;
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

    // =========================================================
    // Apertura/Reset Chat con Gestione della Sessione
    // =========================================================
    chatbotToggler.addEventListener("click", () => {
        // Se la chat è chiusa, la apriamo
        if (!document.body.classList.contains("show-chatbot")) {
            document.body.classList.add("show-chatbot");
            localStorage.setItem("chatOpen", "true");

            // Se non è stato inizializzato (per qualche motivo) ricarichiamo i messaggi
            if (!chatInizializzata) {
                loadSessionMessages();
            }
        } else {
            // =========================================================
            // (3) Se la chat era aperta e riclicchiamo, facciamo un RESET COMPLETO
            // =========================================================
            // 1) Chiudo visivamente
            document.body.classList.remove("show-chatbot");
            chatInizializzata = false;
            localStorage.setItem("chatOpen", "false");

            // 2) Chiamo clearSession sul backend
            $.ajax({
                url: "/faq/clearSession",
                method: "POST",
                success: function () {
                    console.log("Sessione della chat resettata");

                    // 3) Svuoto la chatbox e mostro i messaggi iniziali
                    chatbox.innerHTML = "";
                    addIncomingMessage("Ciao, come posso aiutarti?");
                    addChoiceButtons([
                        { text: "Vuoi che ti aiuti a scegliere il genere Più adatto a te?", dataId: "genere-libri" },
                        { text: "Vuoi che ti aiuti sulle funzionalità del sito?", dataId: "info-sito" }
                    ]);

                    // 4) (Facoltativo) Riapro subito la chat, oppure lascio che l'utente clicchi di nuovo
                    document.body.classList.add("show-chatbot");
                    localStorage.setItem("chatOpen", "true");
                },
                error: function() {
                    console.error("Impossibile resettare la sessione della chat");
                    addIncomingMessage("Errore nel resettare la sessione della chat.");
                }
            });
        }
    });

    // -----------------------------
    // Chiusura con la X in alto
    // -----------------------------
    headerClose?.addEventListener("click", () => {
        document.body.classList.remove("show-chatbot");
        localStorage.setItem("chatOpen", "false");
    });
});
