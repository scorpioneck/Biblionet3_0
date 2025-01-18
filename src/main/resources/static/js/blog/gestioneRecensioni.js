document.addEventListener("DOMContentLoaded", () => {
    const rispondiButtons = document.querySelectorAll(".btn-rispondi");

    rispondiButtons.forEach(button => {
        button.addEventListener("click", () => {
            const commentoId = button.getAttribute("data-commento-id");
            const formRisposta = document.querySelector(`#form-risposta-${commentoId}`);

            // Alterna la visibilità del form
            if (formRisposta.style.display === "none") {
                formRisposta.style.display = "block";
            } else {
                formRisposta.style.display = "none";
            }
        });
    });
});
