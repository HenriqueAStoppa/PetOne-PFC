tailwind.config = {
  theme: {
    extend: {
      colors: {
        'verde-claro': '#d8f3dc',
        'verde-card': '#b7e4c7',
        'verde-escuro': '#1b4332',
        'verde-escuro-hover': '#163326',
        'vermelho-emergencia': '#ef4444',
        'vermelho-hover': '#dc2626',
      },
      fontFamily: {
        sans: ['Poppins', 'sans-serif'],
      }
    }
  }
};

function abrirModalLoginRequired() {
  const modal = document.getElementById('modal-login-required');
  if (!modal) return;
  modal.classList.add('flex');
}

function fecharModalLoginRequired() {
  const modal = document.getElementById('modal-login-required');
  if (!modal) return;
  modal.classList.remove('flex');
}


window.iniciarEmergencia = function () {
  const token = localStorage.getItem('petone_token');

  if (!token) {
    abrirModalLoginRequired();
    return;
  }

  window.location.href = '/pages/Emergencia/emergencia.html';
};

document.addEventListener('DOMContentLoaded', () => {
  const btnClose = document.getElementById('btn-close-login-modal');
  const btnCancel = document.getElementById('btn-cancel-login-modal');
  const btnGoLogin = document.getElementById('btn-go-login');

  if (btnClose) {
    btnClose.onclick = fecharModalLoginRequired;
  }

  if (btnCancel) {
    btnCancel.onclick = fecharModalLoginRequired;
  }

  if (btnGoLogin) {
    btnGoLogin.onclick = () => {
      window.location.href = '/pages/login/index.html';
    };
  }
});
