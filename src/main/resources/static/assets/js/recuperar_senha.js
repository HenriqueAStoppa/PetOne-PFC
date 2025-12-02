document.getElementById('formRecuperar').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const msg = document.getElementById('msg');
    const btn = e.target.querySelector('button');
    
    msg.innerText = 'Enviando...';
    msg.style.color = '#666';
    btn.disabled = true;
    
    try {
        const res = await fetch('/api/auth/recuperar-senha/solicitar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });
        
        // A API sempre retorna OK por segurança (para não revelar se o email existe ou não)
        msg.style.color = 'green';
        msg.innerText = 'Se o email estiver cadastrado, enviamos um token (Verifique o console do servidor para testes).';
        
        // Redireciona para a tela onde a pessoa insere o token e a nova senha
        setTimeout(() => {
            window.location.href = '/pages/ResetarSenha/resetar_senha.html';
        }, 3000);

    } catch(err) {
        msg.style.color = 'red';
        msg.innerText = 'Erro ao conectar. Tente novamente.';
        btn.disabled = false;
    }
});