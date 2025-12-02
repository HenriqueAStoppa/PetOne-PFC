const API_URL = 'http://localhost:8080';

document.addEventListener("DOMContentLoaded", () => {
    const checkboxLgpd = document.getElementById('lgpd-consent');
    const btnCadastrar = document.getElementById('btn-cadastrar');

    checkboxLgpd.addEventListener('change', function() {
        btnCadastrar.disabled = !this.checked;
    });

    document.getElementById('formCadastro').addEventListener('submit', async (e) => {
      e.preventDefault();
      
      if (!checkboxLgpd.checked) {
          alert("Você precisa aceitar os termos da LGPD para continuar.");
          return;
      }

      const msgErro = document.getElementById('msgErro');
      msgErro.textContent = '';
      msgErro.style.display = 'none';

      const senha = document.getElementById('senha').value;
      const senhaConfirma = document.getElementById('senhaConfirma').value;

      if (senha !== senhaConfirma) {
        msgErro.textContent = 'As senhas não coincidem.';
        msgErro.style.display = 'block';
        return;
      }

      const data = {
        nomeCompleto: document.getElementById('nome').value,
        cpf: document.getElementById('cpf').value,
        dataNascimento: document.getElementById('nascimento').value,
        telefoneTutor: document.getElementById('telefone').value,
        emailTutor: document.getElementById('email').value,
        senha: senha
      };

      try {
        // Usa fetch direto pois é uma rota pública (sem token prévio)
        const response = await fetch(`${API_URL}/api/auth/cadastro/tutor`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            // Salva o token retornado e redireciona
            localStorage.setItem('petone_token', result.token);
            localStorage.setItem('petone_user_nome', result.nomeCompleto);
            window.location.href = '/pages/Dashboard/Tutor/dashboard_tutor.html';
        } else {
            const errorText = await response.text();
            msgErro.textContent = `Erro: ${errorText}`;
            msgErro.style.display = 'block';
        }
      } catch (error) {
        console.error(error);
        msgErro.textContent = 'Erro ao conectar com o servidor.';
        msgErro.style.display = 'block';
      }
    });
});