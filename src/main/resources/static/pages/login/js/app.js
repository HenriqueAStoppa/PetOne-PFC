// ===== Config da API =====
const API = 'http://localhost:3000';

// ===== Toggle telas =====
const btnSignin = document.querySelector('#signin');
const btnSignup = document.querySelector('#signup');
const body = document.body;

btnSignin?.addEventListener('click', () => { body.className = 'sign-in-js'; });
btnSignup?.addEventListener('click', () => { body.className = 'sign-up-js'; });

// ===== Util: POST JSON com cookie httpOnly =====
async function postJSON(url, data) {
  const r = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(data)
  });
  const json = await r.json().catch(() => ({}));
  if (!r.ok) throw new Error(json.error || 'Erro na requisição');
  return json;
}

// ===== Util: SHA-256 em hex =====
async function sha256Hex(text) {
  const enc = new TextEncoder().encode(text);
  const digest = await crypto.subtle.digest('SHA-256', enc);
  return [...new Uint8Array(digest)].map(b => b.toString(16).padStart(2, '0')).join('');
}

// ====== USUÁRIO (index.html) ======

// Cadastro de usuário (tutor) — IDs corrigidos: form-cadastro
document.getElementById('form-cadastro')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = document.getElementById('msg-cadastro');
  msg.className = 'msg'; msg.textContent = '';
  try {
    const name = document.getElementById('tutor-nome').value.trim();
    const email = document.getElementById('tutor-email').value.trim();
    const cpf = document.getElementById('tutor-cpf').value.trim();
    const senha = document.getElementById('tutor-senha').value;
    const nascimento = document.getElementById('tutor-data').value || null;
    const ativo = document.getElementById('tutor-ativo').checked;
    const emailVerificado = document.getElementById('tutor-emailver').checked;

    const senhaHash = await sha256Hex(senha);

    // Ajuste os nomes dos campos conforme o seu backend espera:
    const payload = {
      name,
      email,
      cpf,
      senhaHash,
      nascimento,
      ativo,
      emailVerificado
    };

    const resp = await postJSON(`${API}/api/usuario/cadastrar`, payload);
    msg.className = 'msg ok';
    msg.textContent = `Cadastro feito! Bem-vindo, ${resp.name || name}.`;
    // window.location.href = 'dashboard-usuario.html';
  } catch (err) {
    msg.className = 'msg err';
    msg.textContent = err.message;
  }
});

// Login de usuário — ID corrigido: form-login
document.getElementById('form-login')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = document.getElementById('msg-login');
  msg.className = 'msg'; msg.textContent = '';
  try {
    const email = document.getElementById('login-email').value.trim();
    const senha = document.getElementById('login-senha').value;
    const senhaHash = await sha256Hex(senha);

    const resp = await postJSON(`${API}/api/usuario/login`, { email, senhaHash });
    msg.className = 'msg ok';
    msg.textContent = `Login OK para ${resp.email || email}.`;
    // window.location.href = 'dashboard-usuario.html';
  } catch (err) {
    msg.className = 'msg err';
    msg.textContent = err.message;
  }
});

// ====== (opcional) Teste de sessão ======
window.checkME = async function () {
  try {
    const r = await fetch(`${API}/api/me`, { credentials: 'include' });
    const t = await r.text();
    alert(t);
  } catch {
    alert('Falhou /api/me');
  }
};
