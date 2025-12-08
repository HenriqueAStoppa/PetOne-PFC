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

//Função chamada pelo botão da página inicial
window.iniciarEmergencia = function () {
  alert('Para iniciar uma emergência e garantir que tenhamos os dados do seu pet, por favor, faça o login.');
  window.location.href = '/pages/login/index.html';
}
