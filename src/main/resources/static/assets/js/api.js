const API_URL = '/api';
const AUTH_URL = '/api/auth';

//Utilitário para pegar o token
function getToken() {
    return localStorage.getItem('petone_token');
}

//Função genérica de Fetch com Autenticação
async function apiFetch(endpoint, options = {}) {
    const token = getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_URL}${endpoint}`, { ...options, headers });

    //Se der erro 401/403, faz logout
    if (response.status === 401 || response.status === 403) {
        logout();
        return null;
    }

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Erro na requisição");
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return response.json();
    }
    return { ok: true };
}

//Função de Logout
function logout() {
    localStorage.removeItem('petone_token');
    localStorage.removeItem('petone_user_nome');
    localStorage.removeItem('petone_user_id');
    window.location.href = '/pages/login/index.html';
}

//Verifica se tem token ao carregar páginas restritas
function checkAuth() {
    if (!getToken()) {
        window.location.href = '/pages/login/index.html';
    }
}