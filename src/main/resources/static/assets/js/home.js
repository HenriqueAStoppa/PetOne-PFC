// home.js

// Configuração do Tailwind
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

// Lógica da Página
function iniciarEmergencia() {
    const token = localStorage.getItem('petone_token');
    
    if (!token) {
      sessionStorage.setItem('redirect_after_login', 'emergencia.html');
      alert("Para iniciar uma emergência e garantir que tenhamos os dados do seu pet, por favor, faça o login.");
      window.location.href = 'pages/login/index.html'; // Caminho relativo mantido, ou use /pages/login/index.html
    } else {
      window.location.href = 'emergencia.html'; 
    }
}