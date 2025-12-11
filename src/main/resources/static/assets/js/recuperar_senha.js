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
      const res = await fetch(`${AUTH_URL}/recuperar-senha/solicitar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });

      if (!res.ok) {
        const erro = await res.text();
        msg.style.color = 'red';
        msg.innerText = `Erro ao enviar email: ${erro || 'tente novamente.'}`;
        btn.disabled = false;
        return;
      }

      msg.style.color = 'green';
      msg.innerText = 'Se o email estiver cadastrado, enviamos um token.';

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
