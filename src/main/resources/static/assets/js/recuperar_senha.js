// recuperar_senha.js

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('formRecuperar');
  const emailInput = document.getElementById('email');
  const msg = document.getElementById('msg');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = emailInput.value.trim();
    const btn = form.querySelector('button');

    msg.innerText = 'Enviando...';
    msg.style.color = '#666';
    btn.disabled = true;

    try {
      // Usa AUTH_URL do api.js: http://localhost:8080/api/auth
      const res = await fetch(`${AUTH_URL}/recuperar-senha/solicitar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });

      // Por segurança, a API não revela se o email existe
      msg.style.color = 'green';
      msg.innerText = 'Se o email estiver cadastrado, enviamos um token (verifique o console do servidor para testes).';

      // Redireciona para a tela de reset de senha
      setTimeout(() => {
        window.location.href = '/pages/ResetarSenha/resetar_senha.html';
      }, 3000);

    } catch (err) {
      console.error(err);
      msg.style.color = 'red';
      msg.innerText = 'Erro ao conectar. Tente novamente.';
      btn.disabled = false;
    }
  });
});
