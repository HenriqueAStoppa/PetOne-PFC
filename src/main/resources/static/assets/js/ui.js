function confirmDialog(options) {
  const {
    title = "Atenção",
    message = "Tem certeza que deseja continuar?",
    confirmText = "Confirmar",
    cancelText = "Cancelar",
    confirmVariant = "danger",
    onConfirm = () => { },
  } = options || {};

  const modal = document.getElementById("modal-confirm");
  const titleEl = document.getElementById("confirm-title");
  const msgEl = document.getElementById("confirm-message");
  const btnOk = document.getElementById("confirm-ok");
  const btnCancel = document.getElementById("confirm-cancel");

  if (!modal || !titleEl || !msgEl || !btnOk || !btnCancel) return;

  titleEl.textContent = title;
  msgEl.textContent = message;
  btnOk.textContent = confirmText;
  btnCancel.textContent = cancelText;

  if (confirmVariant === "danger") {
    btnOk.className =
      "px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-semibold hover:bg-red-700 shadow-sm";
  } else {
    btnOk.className =
      "px-4 py-2 rounded-lg bg-blue-600 text-white text-sm font-semibold hover:bg-blue-700 shadow-sm";
  }

  const close = () => {
    modal.classList.remove("flex");
    btnOk.onclick = null;
    btnCancel.onclick = null;
  };

  btnCancel.onclick = () => close();

  btnOk.onclick = () => {
    close();
    onConfirm();
  };

  // Mostra modal
  modal.classList.add("flex");
}

// deixa disponível pra outros arquivos JS
window.confirmDialog = confirmDialog;
