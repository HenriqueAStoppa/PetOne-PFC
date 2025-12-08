document.addEventListener("DOMContentLoaded", () => {
    const checkboxLgpd = document.getElementById('lgpd-consent');
    const btnCadastrar = document.getElementById('btn-cadastrar');
    const form = document.getElementById('formCadastro');
    const nascimentoInput = document.getElementById('nascimento');

    //Limitar o datepicker para no máximo 18 anos atrás 
    if (nascimentoInput) {
        const hoje = new Date();
        const limite = new Date(hoje.getFullYear() - 18, hoje.getMonth(), hoje.getDate());
        const yyyy = limite.getFullYear();
        const mm = String(limite.getMonth() + 1).padStart(2, '0');
        const dd = String(limite.getDate()).padStart(2, '0');
        nascimentoInput.max = `${yyyy}-${mm}-${dd}`;
    }

    //Habilita/desabilita o botão conforme o aceite do termo
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
        const dataNascimento = document.getElementById('nascimento').value;

        //Verifica se as senhas coincidem
        if (senha !== senhaConfirma) {
            msgErro.textContent = 'As senhas não coincidem.';
            msgErro.style.display = 'block';
            return;
        }

        //Verifica se é maior de 18 anos
        if (!isMaiorDeIdade(dataNascimento)) {
            msgErro.textContent = 'É necessário ter pelo menos 18 anos para se cadastrar.';
            msgErro.style.display = 'block';
            return;
        }

        const data = {
            nomeCompleto: document.getElementById('nome').value,
            cpf: document.getElementById('cpf').value,
            dataNascimento: dataNascimento,
            telefoneTutor: document.getElementById('telefone').value,
            emailTutor: document.getElementById('email').value,
            senha: senha
        };

        try {
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

function isMaiorDeIdade(dataStr) {
    if (!dataStr) return false;

    const partes = dataStr.split('-');
    if (partes.length !== 3) return false;

    const ano = parseInt(partes[0], 10);
    const mes = parseInt(partes[1], 10);
    const dia = parseInt(partes[2], 10);

    const nascimento = new Date(ano, mes - 1, dia);
    if (isNaN(nascimento.getTime())) return false;

    const hoje = new Date();
    let idade = hoje.getFullYear() - ano;

    const mesAtual = hoje.getMonth() + 1;
    const diaAtual = hoje.getDate();

    // Se ainda não fez aniversário este ano, reduz 1
    if (mesAtual < mes || (mesAtual === mes && diaAtual < dia)) {
        idade--;
    }

    return idade >= 18;
}
