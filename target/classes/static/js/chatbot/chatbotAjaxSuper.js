document.addEventListener("DOMContentLoaded", () => {
    const chatbotToggler = document.querySelector("#chatbot-toggler");
    const headerClose = document.querySelector("#header-close");
    const chatbox = document.querySelector("#chatbox");

    let chatInizializzata = false;

    let chatState = {
        chatAperta: false,
        messaggi: [],
        context: 'initial',
        selectedResponseId: null
    };

    // ---------------------------------------------
    // Funzione di reset
    // ---------------------------------------------
    function resetChat() {
        chatState = {
            chatAperta: false,
            messaggi: [],
            context: 'initial',
            selectedResponseId: null
        };
        chatbox.innerHTML = "";
        chatInizializzata = false;
        salvaStatoChat();

    }

    // ---------------------------------------------
    // Ripristino stato chat da sessionStorage
    // ---------------------------------------------
    function ripristinaStatoChat() {
        const salvato = sessionStorage.getItem("chatState");
        if (salvato) {
            chatState = JSON.parse(salvato);

            if (chatState.chatAperta) {
                document.body.classList.add("show-chatbot");
                chatbox.innerHTML = "";

                if (chatState.messaggi.length > 0) {
                    chatState.messaggi.forEach(msg => {
                        if (msg.tipo === "incoming") {
                            addIncomingMessage(msg.testo, false);
                        } else if (msg.tipo === "outgoing") {
                            addOutgoingMessage(msg.testo, false);
                        }
                    });
                    chatInizializzata = true;
                }

                if (chatState.context === 'risposte') {
                    caricaRisposteAppend();
                } else if (chatState.context === 'domanda' && chatState.selectedResponseId) {
                    caricaDomanda(chatState.selectedResponseId);
                }
            }
        }
    }

    function salvaStatoChat() {
        sessionStorage.setItem("chatState", JSON.stringify(chatState));
    }

    // ---------------------------------------------
    // Funzione di inizializzazione chat
    // ---------------------------------------------
    function initializeChat() {
        if (chatInizializzata || chatState.messaggi.length > 0) {
            return;
        }

        chatbox.innerHTML = "";
        addIncomingMessage("Ciao, come posso aiutarti?");
        addChoiceButtons([
            { text: "Determina genere libri", dataId: "genere-libri" },
            { text: "Info sul sito", dataId: "info-sito" }
        ]);

        chatInizializzata = true;
        chatState.chatAperta = true;
        salvaStatoChat();
    }

    // ---------------------------------------------
    // Apertura/Chiusura/Toggle Chat
    // ---------------------------------------------
    chatbotToggler.addEventListener("click", () => {
        if (!document.body.classList.contains("show-chatbot")) {
            if (!chatInizializzata) {
                initializeChat();
            } else {
                document.body.classList.add("show-chatbot");
                chatState.chatAperta = true;
                salvaStatoChat();
            }
        } else {
            resetChat();
        }
    });

    headerClose?.addEventListener("click", () => {
        document.body.classList.remove("show-chatbot");
        chatState.chatAperta = false;
        salvaStatoChat();
    });

    // ---------------------------------------------
    // Funzioni per i messaggi base
    // ---------------------------------------------
    function addIncomingMessage(testo, salva = true) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
            <span class="material-symbols-outlined">smart_toy</span>
            <p>${testo}</p>
        `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        if (salva) {
            chatState.messaggi.push({ tipo: 'incoming', testo });
            salvaStatoChat();
        }
    }

    function addOutgoingMessage(testo, salva = true) {
        const li = document.createElement("li");
        li.classList.add("chat", "outgoing");
        li.innerHTML = `<p>${testo}</p>`;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        if (salva) {
            chatState.messaggi.push({ tipo: 'outgoing', testo });
            salvaStatoChat();
        }
    }

    // ---------------------------------------------
    // Bottoni di scelta iniziali
    // ---------------------------------------------
    function addChoiceButtons(choices) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span>`;

        const div = document.createElement("div");
        const p = document.createElement("p");

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
                    caricaRisposteAppend();
                }

                li.remove();
            });

            p.appendChild(button);
        });

        div.appendChild(p);
        li.appendChild(div);
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
    }

    // ---------------------------------------------
    // Esempi di altre funzioni esistenti (non cambiate)
    // ---------------------------------------------
    function caricaRisposteAppend() {
        if(chatState.context !== "risposte") {
            addIncomingMessage("Ecco le info sul sito: ....");
        }
        chatState.context = 'risposte';
        salvaStatoChat();

        $.ajax({
            url: "/bot/risposte",
            method: "GET",
            success: function(html) {
                chatbox.insertAdjacentHTML("beforeend", html);
                attachDomandaClickEvents();
                chatbox.scrollTop = chatbox.scrollHeight;
            },
            error: function() {
                addIncomingMessage("Errore nel caricamento delle FAQ.");
            }
        });
    }


    function attachDomandaClickEvents() {
        const bottoniRisposta = document.querySelectorAll(".risposta-button");

        bottoniRisposta.forEach(bottone => {
            bottone.addEventListener("click", () => {
                const rispostaId = bottone.getAttribute("data-id");
                const testoRis = bottone.textContent || "Domanda selezionata";
                addOutgoingMessage(testoRis);

                // Chiamata alla funzione caricaDomanda
                caricaDomanda(rispostaId);

                $.ajax({
                    url: `/bot/${rispostaId}/domanda`,
                    method: "GET",
                    success: function(html) {
                        chatbox.insertAdjacentHTML("beforeend", html);
                        chatbox.scrollTop = chatbox.scrollHeight;
                    },
                    error: function() {
                        addIncomingMessage("Errore nel caricamento delle risposte.");
                    }
                });
            });
        });
    }


    function caricaDomanda(rispostaId) {
        // Aggiungi un messaggio in arrivo per indicare che la domanda viene caricata
        if(chatState.context !== "domanda") {
            addIncomingMessage("Sto caricando la tua domanda...");
        }

        $.ajax({
            url: `/bot/${rispostaId}/domanda`,
            method: "GET",
            success: function(html) {
                // Inserisci l'HTML della domanda nella chat
                chatbox.insertAdjacentHTML("beforeend", html);
                chatbox.scrollTop = chatbox.scrollHeight;

                // Riattacca gli event listener per i nuovi bottoni della domanda
                attachDomandaClickEvents();

                // Aggiorna lo stato della chat
                chatState.context = 'domanda';
                chatState.selectedResponseId = rispostaId;
                salvaStatoChat();
            },
            error: function() {
                addIncomingMessage("Errore nel caricamento della domanda.");
            }
        });
    }

    // ---------------------------------------------
    // Logica Questionario
    // ---------------------------------------------
    function caricaDeterminaGenere() {
        addIncomingMessage("Carico il questionario per determinare il tuo genere preferito...");

        $.ajax({
            url: "/bot/questionario/inizia",
            method: "GET",
            success: function(data) {
                if (data.questionarioCompletato) {
                    addIncomingMessage("Questionario non disponibile o già completato.");
                } else {
                    mostraProssimaDomanda(data.prossimaRisposta, data.indiceRisposta);
                }
            },
            error: function() {
                addIncomingMessage("Errore durante il caricamento del questionario. Riprova più tardi.");
            }
        });
    }

    function mostraProssimaDomanda(risposta, indiceRisposta) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
            <span class="material-symbols-outlined">smart_toy</span>
            <div>
                <p>${risposta.contenuto}</p>
                <div>
                    ${
            risposta.domande.map(domanda => `
                        <button class="questionario-button" data-id="${domanda.idDomanda}">
                            ${domanda.contenuto}
                        </button>
                    `).join("")
        }
                </div>
            </div>
        `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        attachQuestionarioClickEvents(indiceRisposta);
    }

    function attachQuestionarioClickEvents(indiceRisposta) {
        const bottoniQuestionario = document.querySelectorAll(".questionario-button");
        bottoniQuestionario.forEach(bottone => {
            bottone.addEventListener("click", () => {
                bottoniQuestionario.forEach(btn => btn.classList.remove("selected"));
                bottone.classList.add("selected");

                const rispostaSelezionata = parseInt(bottone.dataset.id, 10);

                $.ajax({
                    url: `/bot/nextDomanda/${indiceRisposta}`,
                    method: "GET",
                    data: { domandaId: rispostaSelezionata },
                    success: function(data) {
                        if (data.questionarioCompletato) {
                            addIncomingMessage("Grazie! Hai completato il questionario.");
                            inviaRisposteSelezionate();
                        } else {
                            mostraProssimaDomanda(data.prossimaRisposta, data.indiceRisposta);
                        }
                    },
                    error: function() {
                        addIncomingMessage("Errore durante il caricamento della prossima domanda.");
                    }
                });
            });
        });
    }

    function inviaRisposteSelezionate() {
        const risposteSelezionate = [...document.querySelectorAll(".questionario-button.selected")]
            .map(r => parseInt(r.dataset.id, 10));

        if (risposteSelezionate.length === 0) {
            addIncomingMessage("Seleziona almeno una risposta prima di procedere!");
            return;
        }

        $.ajax({
            url: '/bot/calcolaGenere',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(risposteSelezionate),
            success: function(data) {
                mostraGenerePreferitoInChat(data);
            },
            error: function() {
                addIncomingMessage("Si è verificato un errore durante il calcolo del genere predominante.");
            }
        });
    }

    function mostraGenerePreferitoInChat(genere) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
            <span class="material-symbols-outlined">smart_toy</span>
            <div>
                <p>Ecco il tuo genere preferito:</p>
                <div>
                    <button class="domanda-button" disabled style="opacity:0.7; cursor:not-allowed;">
                        ${genere}
                    </button>
                </div>
            </div>
        `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
    }


    chatbotToggler.addEventListener("click", () => {
        if (!document.body.classList.contains("show-chatbot")) {
            if (!chatInizializzata) {
                initializeChat();
            }
            document.body.classList.add("show-chatbot");
            chatState.chatAperta = true;
        } else {
            document.body.classList.remove("show-chatbot");
            chatState.chatAperta = false;
            salvaStatoChat();
        }
    });

    // Inizializza lo stato della chat al caricamento
    ripristinaStatoChat();
});
