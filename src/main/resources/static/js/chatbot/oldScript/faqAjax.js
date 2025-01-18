document.addEventListener("DOMContentLoaded", () => {
    const chatbotToggler = document.querySelector("#chatbot-toggler"); // Pulsante in basso
    const headerClose = document.querySelector("#header-close");       // X in alto
    const chatbox = document.querySelector("#chatbox");               // ul.chatbox

    let chatInizializzata = false;

    // Messaggio del bot (incoming)
    function addIncomingMessage(testo) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `
            <span class="material-symbols-outlined">smart_toy</span>
            <p>${testo}</p>
        `;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
    }

    // Messaggio dell'utente (outgoing)
    function addOutgoingMessage(testo) {
        const li = document.createElement("li");
        li.classList.add("chat", "outgoing");
        li.innerHTML = `<p>${testo}</p>`;
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
    }

    // Aggiunge i bottoni di scelta iniziali (incoming)
    function addChoiceButtons(choices) {
        const li = document.createElement("li");
        li.classList.add("chat", "incoming");
        li.innerHTML = `<span class="material-symbols-outlined">smart_toy</span>`;

        const div = document.createElement("div");
        const p = document.createElement("p");

        choices.forEach(choice => {
            const button = document.createElement("button");
            button.classList.add("scelta-button","chatbot-button");
            button.setAttribute("data-id", choice.dataId);
            button.textContent = choice.text;

            button.addEventListener("click", () => {
                addOutgoingMessage(choice.text);
                if (choice.dataId === "genere-libri") {
                    caricaDeteriminaGenere();
                } else if (choice.dataId === "info-sito") {
                    caricaInfoSito();
                }

                // Rimuove i bottoni iniziali
                li.remove();
            });

            p.appendChild(button);
        });

        div.appendChild(p);
        li.appendChild(div);
        chatbox.appendChild(li);
        chatbox.scrollTop = chatbox.scrollHeight;
    }

    // Carica l'elenco domande da /faq/domande (append)
    function caricaDomandeAppend() {
        $.ajax({
            url: "/faq/domande",
            method: "GET",
            success: function(html) {
                chatbox.insertAdjacentHTML("beforeend", html);
                attachDomandaClickEvents();
                chatbox.scrollTop = chatbox.scrollHeight;
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
                const domandaId = bottone.getAttribute("data-id");
                const testoDomanda = bottone.textContent || "Domanda selezionata";
                addOutgoingMessage(testoDomanda);

                $.ajax({
                    url: `/faq/${domandaId}/risposte`,
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

    // “Info sul sito”: messaggio + appendo domande
    function caricaInfoSito() {
        addIncomingMessage("Ecco le info sul sito: ....");
        caricaDomandeAppend();
    }

    // “Determina genere libri”
    function caricaDeteriminaGenere() {
        addIncomingMessage("Da implementare...");
    }

    // Apertura/Reset col pulsante in basso
    chatbotToggler.addEventListener("click", () => {
        // Se la chat è CHIUSA, la apriamo
        if (!document.body.classList.contains("show-chatbot")) {
            if (!chatInizializzata) {
                // Inizializziamo solo la prima volta
                chatbox.innerHTML = "";
                addIncomingMessage("Ciao, come posso aiutarti?");
                addChoiceButtons([
                    { text: "Determina genere libri", dataId: "genere-libri" },
                    { text: "Info sul sito", dataId: "info-sito" }
                ]);
                chatInizializzata = true;
            }
            document.body.classList.add("show-chatbot");
        }
        // Se la chat è APERTA, la resettiamo
        else {
            // Svuotiamo la chat
            chatbox.innerHTML = "";
            chatInizializzata = false;

            // Ricarichiamo il messaggio e le scelte
            addIncomingMessage("Ciao, come posso aiutarti?");
            addChoiceButtons([
                { text: "Determina genere libri", dataId: "genere-libri" },
                { text: "Info sul sito", dataId: "info-sito" }
            ]);
        }
    });

    // Chiudi la chat con la X in alto → solo nascondi
    headerClose?.addEventListener("click", () => {
        document.body.classList.remove("show-chatbot");
    });
});
