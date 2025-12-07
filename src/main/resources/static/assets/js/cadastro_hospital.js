// cadastro_hospital.js

// Função para buscar CEP na API ViaCEP
async function buscarCep() {
  const cepInput = document.getElementById("cep");
  const cep = cepInput.value.replace(/\D/g, "");

  if (cep.length !== 8) return;

  try {
    const logradouro = document.getElementById("logradouro");
    const bairro = document.getElementById("bairro");
    const cidade = document.getElementById("cidade");
    const uf = document.getElementById("uf");

    logradouro.value = "...";

    const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const data = await res.json();

    if (!data.erro) {
      logradouro.value = data.logradouro;
      bairro.value = data.bairro;
      cidade.value = data.localidade;
      uf.value = data.uf;

      logradouro.disabled = false;
      bairro.disabled = false;
      cidade.disabled = false;
      uf.disabled = false;

      document.getElementById("numero").focus();
    } else {
      alert("CEP não encontrado.");
      logradouro.value = "";
      bairro.value = "";
      cidade.value = "";
      uf.value = "";
    }
  } catch (e) {
    alert("Erro ao buscar CEP.");
  }
}

// Lógica do Form
document.addEventListener("DOMContentLoaded", () => {
  const checkboxLgpd = document.getElementById("lgpd-consent");
  const btnCadastrar = document.getElementById("btn-cadastrar");
  const form = document.getElementById("formCadastro");
  const msgErro = document.getElementById("msgErro");

  // Libera botão só com LGPD
  checkboxLgpd.addEventListener("change", function () {
    btnCadastrar.disabled = !this.checked;
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    msgErro.innerText = "";
    msgErro.style.display = "none";

    if (
      document.getElementById("senha").value !==
      document.getElementById("senhaConfirma").value
    ) {
      msgErro.innerText = "Senhas não coincidem.";
      msgErro.style.display = "block";
      return;
    }

    btnCadastrar.disabled = true;
    btnCadastrar.innerText = "Cadastrando...";

    const data = {
      nomeFantasia: document.getElementById("nome").value,
      cnpj: document.getElementById("cnpj").value,
      emailHospital: document.getElementById("email").value,
      telefoneHospital: document.getElementById("telefone").value,

      cep: document.getElementById("cep").value,
      logradouro: document.getElementById("logradouro").value,
      numero: document.getElementById("numero").value,
      bairro: document.getElementById("bairro").value,
      complemento: document.getElementById("complemento").value,
      cidade: document.getElementById("cidade").value,
      uf: document.getElementById("uf").value,

      veterinarioResponsavel: document.getElementById("vet").value,
      crmvVeterinario: document.getElementById("crmv").value,
      classificacaoServico: parseInt(
        document.getElementById("classificacao").value,
        10
      ),
      senha: document.getElementById("senha").value,
    };

    try {
      // Usa AUTH_URL definido em api.js (http://localhost:8080/api/auth)
      const endpoint = `${AUTH_URL}/cadastro/hospital`;

      const response = await fetch(endpoint, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        alert("Cadastro realizado! Endereço validado e coordenadas geradas.");
        // redireciona para a página de login estática
        window.location.href = "/pages/login/index.html";
      } else {
        const errorText = await response.text();
        msgErro.innerText = `Erro: ${errorText || "Falha ao cadastrar."}`;
        msgErro.style.display = "block";
        btnCadastrar.disabled = false;
        btnCadastrar.innerText = "Cadastrar Unidade";
      }
    } catch (error) {
      console.error(error);
      msgErro.innerText = "Erro ao conectar com o servidor.";
      msgErro.style.display = "block";
      btnCadastrar.disabled = false;
      btnCadastrar.innerText = "Cadastrar Unidade";
    }
  });
});
