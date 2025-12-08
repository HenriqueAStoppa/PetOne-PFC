document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('formReset');
  const msg = document.getElementById('msg');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const token = document.getElementById('token').value;
    const novaSenha = document.getElementById('senha').value;
    const btn = form.querySelector('button[type="submit"]');

    msg.innerText = 'Processando...';
    msg.style.color = '#666';
    btn.disabled = true;

    const data = { token, novaSenha };

    try {
      const res = await fetch('/api/auth/recuperar-senha/resetar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      });

      if (res.ok) {
        msg.style.color = 'green';
        msg.innerText = 'Senha alterada com sucesso! Redirecionando...';
        setTimeout(() => window.location.href = '/pages/login/index.html', 2000);
      } else {
        const erro = await res.text();
        msg.style.color = 'red';
        msg.innerText = `Erro: ${erro}`;
        btn.disabled = false;
      }
    } catch (err) {
      msg.style.color = 'red';
      msg.innerText = 'Erro ao conectar. Tente novamente.';
      btn.disabled = false;
    }
  });
});
