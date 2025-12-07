// cadastro_tutor.js

document.addEventListener("DOMContentLoaded", () => {
    const checkboxLgpd = document.getElementById('lgpd-consent');
    const btnCadastrar = document.getElementById('btn-cadastrar');
    const form = document.getElementById('formCadastro');

    // Habilita/desabilita o botão conforme o aceite do termo
    checkboxLgpd.addEventListener('change', function () {
        btnCadastrar.disabled = !this.checked;
    });

    form.addEventListener('submit', async (e) => {
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
            // Usa AUTH_URL do api.js: http://localhost:8080/api/auth
            const endpoint = `${AUTH_URL}/cadastro/tutor`;

            const response = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                const result = await response.json();

                // Salva token e infos básicas do usuário
                if (result.token) {
                    localStorage.setItem('petone_token', result.token);
                }
                localStorage.setItem('petone_user_nome', result.nomeCompleto || '');
                if (result.idTutor || result.id) {
                    localStorage.setItem('petone_user_id', result.idTutor || result.id);
                }

                // Redireciona para o dashboard de tutor
                window.location.href = '/pages/Dashboard/Tutor/dashboard_tutor.html';
            } else {
                const errorText = await response.text();
                msgErro.textContent = `Erro: ${errorText || 'Falha ao cadastrar.'}`;
                msgErro.style.display = 'block';
            }
        } catch (error) {
            console.error(error);
            msgErro.textContent = 'Erro ao conectar com o servidor.';
            msgErro.style.display = 'block';
        }
    });
});
