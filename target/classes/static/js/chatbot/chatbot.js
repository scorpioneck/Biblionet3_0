document.addEventListener("DOMContentLoaded", () => {
    // Selettori principali
    const chatbotToggler = document.querySelector("#chatbot-toggler");
    const headerClose = document.querySelector("#header-close");
    const chatbox = document.querySelector("#chatbox");

    // Verifica esistenza degli elementi
    if (!chatbotToggler || !chatbox) {
        console.error("Elemento #chatbot-toggler o #chatbox non trovato nel DOM.");
        return;
    }

    // Flag e stato della chat
    let chatInizializzata = false;


    let chatState = {
        chatAperta: false,
        messaggi: [],
        context: "initial",
        selectedResponseId: null,
        currentChoices: [],
        tipoUtente : null,
    };

    // ---------------------------------------------------
    // Verifica utente + ripristino chat (punto di ingresso)
    // ---------------------------------------------------
    verificaStatoUtente();

    // Evento Toggle Chatbot
    chatbotToggler.addEventListener("click", handleChatbotTogglerClick);

    // Evento Close Header
    headerClose?.addEventListener("click", handleHeaderClose);

    // ---------------------------------------------------
    // Funzione: Verifica stato utente
    // ---------------------------------------------------
    function verificaStatoUtente() {
        $.ajax({
            url: "/bot/verificaUtente",
            method: "GET",
            success: function (data) {
                if (data.logged) {
                    console.log("[verificaStatoUtente] Utente loggato:", data.nome);

                    // Se abbiamo chatState in sessionStorage, lo ripristiniamo
                    const salvato = sessionStorage.getItem("chatState");
                    const tipoUtenteSalvato = sessionStorage.getItem("tipoUtente");

                    if (salvato && tipoUtenteSalvato === data.nome) {
                           ripristinaStatoChat();

                    } else {
                        sessionStorage.setItem("tipoUtente", data.nome);
                        chatState.tipoUtente = data.nome;
                        console.log("TipoUtente salvatoPrimadiInitialate:"+ chatState.tipoUtente);
                        initializeChat();

                    }
                } else {
                    console.warn("[verificaStatoUtente] Utente non loggato. Resetto la chat.");
                    resetChat();
                }
            },
            error: function (xhr) {
                console.error("[verificaStatoUtente] Errore AJAX:", xhr.responseText);
                resetChat();
            },
        });
    }

    // ---------------------------------------------------
    // Funzione: Initialize Chat
    // ---------------------------------------------------
    function initializeChat() {
        if (chatInizializzata || chatState.messaggi.length > 0) {
            console.log("[initializeChat] Chat già inizializzata o con messaggi, esco.");
            return;
        }
        console.log("[initializeChat] Inizializzo la chat da zero.");

        chatbox.innerHTML = "";
        addIncomingMessage("Ciao, come posso aiutarti?");

        const choices = [
            { text: "Vuoi che ti aiuti a determinare il tuo genere", dataId: "genere-libri" },
            { text: "Vuoi che ti aiuti descriva le funzionalità del sito", dataId: "info-sito" },
        ];
        addChoiceButtons(choices);

        chatState.currentChoices = choices;
        chatInizializzata = true;
        chatState.chatAperta = true;
        salvaStatoChat();
    }

    // ---------------------------------------------------
    // Funzione: Ripristina chat da sessionStorage
    // ---------------------------------------------------
    function ripristinaStatoChat() {
        const salvato = sessionStorage.getItem("chatState");
        if (!salvato) {
            console.log("[ripristinaStatoChat] Nessuno stato salvato, inizializzo chat.");
            initializeChat();
            return;
        }

        chatState = JSON.parse(salvato);
        console.log("[ripristinaStatoChat] Stato ripristinato:", chatState);

        if (chatState.chatAperta) {
            document.body.classList.add("show-chatbot");
            chatbox.innerHTML = "";

            // Ricostruisco i messaggi
            chatState.messaggi.forEach((msg) => {
                if (msg.tipo === "incoming") {
                    addIncomingMessage(msg.testo, false);
                } else if (msg.tipo === "outgoing") {
                    addOutgoingMessage(msg.testo, false);
                }
            });

            // Ricostruisco il contesto
            if (chatState.context === "risposte") {
                caricaRisposteAppend();
            } else if (chatState.context === "domanda" && chatState.selectedResponseId) {
                caricaDomanda(chatState.selectedResponseId);
            } else if (chatState.context === "scelta") {
                addChoiceButtons(chatState.currentChoices);
            }

            chatInizializzata = true;
        } else {
            console.log("[ripristinaStatoChat] La chat era chiusa, non ricostruisco messaggi.");
            initializeChat();
        }
    }

    // ---------------------------------------------------
    // Funzione: Salva Stato Chat su sessionStorage
    // ---------------------------------------------------
    function salvaStatoChat() {
        sessionStorage.setItem("chatState", JSON.stringify(chatState));
        sessionStorage.setItem("tipoUtente ", chatState.tipoUtente);
        console.log("[salvaStatoChat] Stato salvato:", chatState);
        console.log("[salvaStatoChat] Stato tipoUtente salvato", chatState.tipoUtente);
    }

    // ---------------------------------------------------
    // Funzione: Reset Chat
    // ---------------------------------------------------
    function resetChat() {
        console.log("[resetChat] Resetto lo stato e chiudo la chat");

        chatState = {
            chatAperta: false,
            messaggi: [],
            context: "initial",
            selectedResponseId: null,
            currentChoices: [],
        };

        chatbox.innerHTML = "";
        chatInizializzata = false;
        salvaStatoChat();

    }

    // ---------------------------------------------------
    // Event Handler: Chiudi Chat con "X" header
    // ---------------------------------------------------
    function handleHeaderClose() {
        console.log("[headerClose] Chat chiusa da headerClose");
        document.body.classList.remove("show-chatbot");
        chatState.chatAperta = false;
        salvaStatoChat();
    }

    // ---------------------------------------------------
    // Event Handler: Toggle Chat
    // ---------------------------------------------------
    function handleChatbotTogglerClick() {
        if (!document.body.classList.contains("show-chatbot")) {
            // Caso: Chat chiusa, aprila
            if (!chatInizializzata) {
                initializeChat(); // Inizializza la chat solo se necessario
            }

            document.body.classList.add("show-chatbot");
            chatState.chatAperta = true;
            salvaStatoChat();
        } else {
            // Caso: Chat aperta, chiudila senza resettare
            chatState.chatAperta = false;
            resetChat()
            // Non chiamare resetChat() qui per evitare il reset della DOM
        }

        ripristinaStatoChat();
    }

    // ---------------------------------------------------
    // Messaggi
    // ---------------------------------------------------
    function addIncomingMessage(testo, salva = true) {
        if (!testo) return;

        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
      <span class="material-symbols-outlined">smart_toy</span>
      <p>${testo}</p>
    `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        if (salva) {
            chatState.messaggi.push({ tipo: "incoming", testo });
            salvaStatoChat();
        }
    }

    function addOutgoingMessage(testo, salva = true) {
        if (!testo) return;

        const li = document.createElement("li");
        li.classList.add("chat", "outgoing");
        li.innerHTML = `<p>${testo}</p>`;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        if (salva) {
            chatState.messaggi.push({ tipo: "outgoing", testo });
            salvaStatoChat();
        }
    }

    // ---------------------------------------------------
    // Funzione: Aggiungi Bottoni di Scelta
    // ---------------------------------------------------
    function addChoiceButtons(choices) {
        if (!choices || choices.length === 0) return;

        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span>`;

        const div = document.createElement("div");
        const p = document.createElement("p");

        choices.forEach((choice) => {
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
                // Rimuovo i bottoni dopo aver cliccato
                li.remove();

                // Svuoto currentChoices poiché ho cliccato
                chatState.currentChoices = [];
                salvaStatoChat();
            });

            p.appendChild(button);
        });

        div.appendChild(p);
        li.appendChild(div);
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        // Stato = scelta
        chatState.context = "scelta";
        chatState.currentChoices = choices;
        salvaStatoChat();
    }

    // ---------------------------------------------------
    // Carica Risposte FAQ
    // ---------------------------------------------------
    function caricaRisposteAppend() {
        if (chatState.context !== "risposte") {
            addIncomingMessage("Ti elenco le funzionalità: ....");
        }
        chatState.context = "risposte";
        salvaStatoChat();

        $.ajax({
            url: "/bot/risposte",
            method: "GET",
            success: function (html) {
                chatbox.insertAdjacentHTML("beforeend", html);
                attachDomandaClickEvents();
                chatbox.scrollTop = chatbox.scrollHeight;
            },
            error: function () {
                addIncomingMessage("Errore nel caricamento delle risposte.");
            },
        });
    }

    // ---------------------------------------------------
    // Attacca Eventi: Domanda
    // ---------------------------------------------------
    function attachDomandaClickEvents() {
        const bottoniRisposta = document.querySelectorAll(".risposta-button");

        bottoniRisposta.forEach((bottone) => {
            bottone.addEventListener("click", () => {
                const rispostaId = bottone.getAttribute("data-id");
                const testoRis = bottone.textContent || "Domanda selezionata";
                const linkRisposta = bottone.getAttribute("data-link")
                addOutgoingMessage(testoRis);

                // Rimuovo il contenitore delle risposte precedenti
                const parentLi = bottone.closest("li");
                if (parentLi) {
                    parentLi.remove();
                }

                if (linkRisposta) {
                    localStorage.setItem("chatOpen", "true");
                    window.location.href = linkRisposta;
                }

                // Carico la domanda
                caricaDomanda(rispostaId);
            });
        });
    }

    // ---------------------------------------------------
    // Carica Domanda
    // ---------------------------------------------------
    function caricaDomanda(rispostaId) {
        if (chatState.context !== "domanda") {
            addIncomingMessage("Sto caricando le tue possibili domande e soluzioni...");
        }

        $.ajax({
            url: `/bot/${rispostaId}/domanda`,
            method: "GET",
            success: function (html) {
                chatbox.insertAdjacentHTML("beforeend", html);
                chatbox.scrollTop = chatbox.scrollHeight;

                attachDomandaClickEvents();

                chatState.context = "domanda";
                chatState.selectedResponseId = rispostaId;
                salvaStatoChat();
            },
            error: function () {
                addIncomingMessage("Errore nel caricamento della possibile domanda alla risposta.");
            },
        });
    }

    // ---------------------------------------------------
    // Logica Questionario
    // ---------------------------------------------------
    function caricaDeterminaGenere() {
        addIncomingMessage("Carico il questionario per determinare il tuo genere preferito...");

        $.ajax({
            url: "/bot/questionario/inizia",
            method: "GET",
            success: function (data) {
                if (data.questionarioCompletato) {
                    addIncomingMessage("Questionario non disponibile o già completato.");
                } else {
                    mostraProssimaDomanda(data.prossimaRisposta, data.indiceRisposta);
                }
            },
            error: function () {
                addIncomingMessage("Errore durante il caricamento del questionario. Riprova più tardi.");
            },
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
              ${risposta.domande
            .map(
                (domanda) => `
                <button class="questionario-button" data-id="${domanda.idDomanda}">
                    ${domanda.contenuto}
                </button>
              `
            )
            .join("")}
          </div>
      </div>
    `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;

        attachQuestionarioClickEvents(indiceRisposta);
    }

    function attachQuestionarioClickEvents(indiceRisposta) {
        const bottoniQuestionario = document.querySelectorAll(".questionario-button");
        bottoniQuestionario.forEach((bottone) => {
            bottone.addEventListener("click", () => {
                bottoniQuestionario.forEach((btn) => btn.classList.remove("selected"));
                bottone.classList.add("selected");

                const rispostaSelezionata = parseInt(bottone.dataset.id, 10);

                $.ajax({
                    url: `/bot/nextDomanda/${indiceRisposta}`,
                    method: "GET",
                    data: { domandaId: rispostaSelezionata },
                    success: function (data) {
                        if (data.questionarioCompletato) {
                            addIncomingMessage("Grazie! Hai completato il questionario.");
                            inviaRisposteSelezionate();
                        } else {
                            mostraProssimaDomanda(data.prossimaRisposta, data.indiceRisposta);
                        }
                    },
                    error: function () {
                        addIncomingMessage("Errore durante il caricamento della prossima domanda.");
                    },
                });
            });
        });
    }

    function inviaRisposteSelezionate() {
        const risposteSelezionate = [...document.querySelectorAll(".questionario-button.selected")].map(
            (r) => parseInt(r.dataset.id, 10)
        );

        if (risposteSelezionate.length === 0) {
            addIncomingMessage("Seleziona almeno una risposta prima di procedere!");
            return;
        }

        $.ajax({
            url: "/bot/calcolaGenere",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(risposteSelezionate),
            success: function (data) {
                mostraGenerePreferitoInChat(data);
            },
            error: function () {
                addIncomingMessage("Si è verificato un errore durante il calcolo del genere predominante.");
            },
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
});
