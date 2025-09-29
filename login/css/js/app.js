// ===== Config da API (igual ao seu server .env FRONT_ORIGIN) =====
const API = 'http://localhost:3000';

// ===== Animação/toggle que você já tinha =====
const btnSignin = document.querySelector('#signin');
const btnSignup = document.querySelector('#signup');
const body = document.body;

btnSignin?.addEventListener('click', () => { body.className = 'sign-in-js'; });
btnSignup?.addEventListener('click', () => { body.className = 'sign-up-js'; });

// ===== Helper para POST JSON com cookie httpOnly =====
async function postJSON(url, data) {
  const r = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',      // envia/recebe cookie
    body: JSON.stringify(data)
  });
  const json = await r.json().catch(() => ({}));
  if (!r.ok) throw new Error(json.error || 'Erro na requisição');
  return json;
}

// ====== USUÁRIO (index.html) ======

// Cadastro de usuário (tutor)
document.getElementById('form-cadastro-usuario')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    const name = document.getElementById('tutor-nome').value.trim();
    const email = document.getElementById('tutor-email').value.trim();
    const password = document.getElementById('tutor-senha').value;

    const resp = await postJSON(`${API}/api/usuario/cadastrar`, { name, email, password });
    alert(`Bem-vindo, ${resp.name}!`);
    // window.location.href = 'dashboard-usuario.html';
  } catch (err) {
    alert(err.message);
  }
});

// Login de usuário
document.getElementById('form-login-usuario')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-senha').value;

    const resp = await postJSON(`${API}/api/usuario/login`, { email, password });
    alert(`Login OK: ${resp.email}`);
    // window.location.href = 'dashboard-usuario.html';
  } catch (err) {
    alert(err.message);
  }
});

// ====== HOSPITAL (hospital.html) ======

// Cadastro de hospital/profissional
document.getElementById('form-cadastro-hospital')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    const name = document.getElementById('rt-nome').value.trim();
    const crmv = document.getElementById('rt-crmv')?.value.trim() || null;
    const email = document.getElementById('rt-email').value.trim();
    const password = document.getElementById('rt-senha').value;

    const resp = await postJSON(`${API}/api/hospital/cadastrar`, { name, crmv, email, password });
    alert(`Clínica cadastrada! RT: ${resp.name}`);
    // window.location.href = 'dashboard-hospital.html';
  } catch (err) {
    alert(err.message);
  }
});

// Login do hospital
document.getElementById('form-login-hospital')?.addEventListener('submit', async (e) => {
  e.preventDefault();
  try {
    const email = document.getElementById('hospital-email').value.trim();
    const password = document.getElementById('hospital-senha').value;

    const resp = await postJSON(`${API}/api/hospital/login`, { email, password });
    alert(`Login Hospital OK: ${resp.email}`);
    // window.location.href = 'dashboard-hospital.html';
  } catch (err) {
    alert(err.message);
  }
});

// ====== (opcional) Teste rápido de sessão ======
window.checkME = async function() {
  try {
    const r = await fetch(`${API}/api/me`, { credentials: 'include' });
    const t = await r.text();
    alert(t);
  } catch {
    alert('Falhou /api/me');
  }
};
