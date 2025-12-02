const API_URL = 'http://localhost:8080';

// Função para buscar CEP na API ViaCEP
async function buscarCep() {
    const cep = document.getElementById('cep').value.replace(/\D/g, '');
    if (cep.length !== 8) return;

    try {
        document.getElementById('logradouro').value = "...";
        
        const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        const data = await res.json();

        if (!data.erro) {
            document.getElementById('logradouro').value = data.logradouro;
            document.getElementById('bairro').value = data.bairro;
            document.getElementById('cidade').value = data.localidade;
            document.getElementById('uf').value = data.uf;
            
            document.getElementById('logradouro').disabled = false;
            document.getElementById('bairro').disabled = false;
            document.getElementById('cidade').disabled = false;
            document.getElementById('uf').disabled = false;
            
            document.getElementById('numero').focus();
        } else {
            alert("CEP não encontrado.");
            document.getElementById('logradouro').value = "";
        }
    } catch (e) {
        alert("Erro ao buscar CEP.");
    }
}

// Lógica do Form
document.addEventListener("DOMContentLoaded", () => {
    const checkboxLgpd = document.getElementById('lgpd-consent');
    const btnCadastrar = document.getElementById('btn-cadastrar');
    
    // Libera botão só com LGPD
    checkboxLgpd.addEventListener('change', function() { btnCadastrar.disabled = !this.checked; });

    document.getElementById('formCadastro').addEventListener('submit', async (e) => {
      e.preventDefault();
      
      const msgErro = document.getElementById('msgErro');
      msgErro.innerText = "";

      if(document.getElementById('senha').value !== document.getElementById('senhaConfirma').value) {
          msgErro.innerText = "Senhas não coincidem.";
          return;
      }

      btnCadastrar.disabled = true;
      btnCadastrar.innerText = "Cadastrando...";

      const data = {
        nomeFantasia: document.getElementById('nome').value,
        cnpj: document.getElementById('cnpj').value,
        emailHospital: document.getElementById('email').value,
        telefoneHospital: document.getElementById('telefone').value,
        
        cep: document.getElementById('cep').value,
        logradouro: document.getElementById('logradouro').value,
        numero: document.getElementById('numero').value,
        bairro: document.getElementById('bairro').value,
        complemento: document.getElementById('complemento').value,
        cidade: document.getElementById('cidade').value,
        uf: document.getElementById('uf').value,
        
        veterinarioResponsavel: document.getElementById('vet').value,
        crmvVeterinario: document.getElementById('crmv').value,
        classificacaoServico: parseInt(document.getElementById('classificacao').value),
        senha: document.getElementById('senha').value
      };

      try {
        const response = await fetch(`${API_URL}/api/auth/cadastro/hospital`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert("Cadastro realizado! Endereço validado e coordenadas geradas.");
            window.location.href = '/pages/login/index.html'; 
        } else {
            const errorText = await response.text();
            msgErro.innerText = `Erro: ${errorText}`;
            btnCadastrar.disabled = false;
            btnCadastrar.innerText = "Cadastrar Unidade";
        }
      } catch (error) {
        msgErro.innerText = 'Erro ao conectar com o servidor.';
        btnCadastrar.disabled = false;
        btnCadastrar.innerText = "Cadastrar Unidade";
      }
    });
});