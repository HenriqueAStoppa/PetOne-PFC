// ===== Base da API (ajuste se necessário) =====
const API = "http://localhost:3000";

// ===== Toggle Signin/Signup (seu efeito) =====
const btnSignin = document.querySelector("#signin");
const btnSignup = document.querySelector("#signup");
const body = document.body;

btnSignin?.addEventListener("click", () => { body.className = "sign-in-js"; });
btnSignup?.addEventListener("click", () => { body.className = "sign-up-js"; });

// ===== Helpers =====
async function postJSON(url, data) {
  const r = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(data),
  });
  const json = await r.json().catch(() => ({}));
  if (!r.ok) throw new Error(json.error || json.message || "Erro na requisição");
  return json;
}

async function sha256Hex(text) {
  const enc = new TextEncoder().encode(text);
  const buf = await crypto.subtle.digest("SHA-256", enc);
  return [...new Uint8Array(buf)].map(b => b.toString(16).padStart(2, "0")).join("");
}

function brDateToISO(v) {
  if (!v) return "";
  const m = v.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
  if (!m) return v; 
  const [, dd, mm, yyyy] = m;
  return `${yyyy}-${mm}-${dd}`;
}


document.getElementById("form-cadastro")?.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const nome  = document.getElementById("tutor-nome").value.trim();
    const email = document.getElementById("tutor-email").value.trim();
    const cpf   = document.getElementById("tutor-cpf")?.value.replace(/\D/g, "") || "";
    const senha = document.getElementById("tutor-senha").value;

    const dataBrOuIso = document.getElementById("tutor-data")?.value || "";
    const dataNasc    = brDateToISO(dataBrOuIso);
    const ativo = !!document.getElementById("tutor-ativo")?.checked;
    const emailVerificado = !!document.getElementById("tutor-emailver")?.checked;

    if (cpf && cpf.length !== 11) throw new Error("CPF deve ter 11 dígitos numéricos.");
    if (nome.length < 2 || nome.length > 100) throw new Error("Nome deve ter entre 2 e 100 caracteres.");
    if (!email) throw new Error("E-mail é obrigatório.");
    if (senha.length < 6) throw new Error("Senha deve ter ao menos 6 caracteres.");


    const senhaHash = await sha256Hex(senha);


    const url = `${API}/api/usuario/cadastrar`;

    const resp = await postJSON(url, {

      name: nome,
      email,
      password: senha,     
     
      cpf,
      senhaHash,
      dataNasc,          
      ativo,
      emailVerificado,
    });

    alert(`Bem-vindo, ${resp.name || nome}!`);
   
  } catch (err) {
    alert(err.message);
  }
});

// Login do Tutor/Usuário
document.getElementById("form-login")?.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const email = document.getElementById("login-email").value.trim();
    const password = document.getElementById("login-senha").value;
    if (!email || !password) throw new Error("Informe e-mail e senha.");

    const resp = await postJSON(`${API}/api/usuario/login`, { email, password });
    alert(`Login OK: ${resp.email || email}`);

  } catch (err) {
    alert(err.message);
  }
});


document.getElementById("form-cadastro-hospital")?.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const nomeClinica = document.getElementById("clinica-nome").value.trim();
    const cnpj        = document.getElementById("clinica-cnpj").value.trim();
    const telefone    = document.getElementById("clinica-telefone").value.trim();
    const endereco    = document.getElementById("clinica-endereco").value.trim();
    const email       = document.getElementById("rt-email").value.trim();
    const password    = document.getElementById("rt-senha").value;

    if (!nomeClinica) throw new Error("Informe o nome da clínica/hospital.");
    if (!telefone) throw new Error("Informe o telefone/WhatsApp.");
    if (!endereco) throw new Error("Informe o endereço.");
    if (!email) throw new Error("Informe o e-mail.");
    if (!password) throw new Error("Crie uma senha.");
    const resp = await postJSON(`${API}/api/hospital/cadastrar`, {
      name: nomeClinica,
      cnpj: cnpj || null,
      phone: telefone,
      address: endereco,
      email,
      password,
    });

    alert(`Clínica cadastrada: ${resp.name || nomeClinica}`);
  } catch (err) {
    alert(err.message);
  }
});

// Login do hospital
document.getElementById("form-login-hospital")?.addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    const email = document.getElementById("hospital-email").value.trim();
    const password = document.getElementById("hospital-senha").value;
    if (!email || !password) throw new Error("Informe e-mail e senha.");

    const resp = await postJSON(`${API}/api/hospital/login`, { email, password });
    alert(`Login Hospital OK: ${resp.email || email}`);
  } catch (err) {
    alert(err.message);
  }
});

window.checkME = async function () {
  try {
    const r = await fetch(`${API}/api/me`, { credentials: "include" });
    const t = await r.text();
    alert(t);
  } catch {
    alert("Falhou /api/me");
  }
};
