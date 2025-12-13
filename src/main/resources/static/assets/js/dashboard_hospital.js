let isUpdating = false;

document.addEventListener("DOMContentLoaded", () => {
    if (window.lucide) lucide.createIcons();
});

function showToast(message, variant = 'success') {
    const toast = document.getElementById('toast');
    const toastInner = document.getElementById('toast-inner');
    const toastMsg = document.getElementById('toast-message');

    if (!toast || !toastInner || !toastMsg) {
        console.warn('Toast não encontrado no DOM.');
        return;
    }

    toastMsg.textContent = message;

    toastInner.className = 'px-4 py-3 rounded-lg shadow-lg flex items-center gap-2 text-sm';

    if (variant === 'success') {
        toastInner.classList.add('bg-green-600', 'text-white');
    } else if (variant === 'error') {
        toastInner.classList.add('bg-red-600', 'text-white');
    } else {
        toastInner.classList.add('bg-blue-600', 'text-white');
    }

    toast.classList.remove('hidden');

    setTimeout(() => {
        toast.classList.add('hidden');
    }, 3000);
}

function confirmDialog(options) {
    const {
        title = "Atenção",
        message = "Tem certeza que deseja continuar?",
        confirmText = "Confirmar",
        cancelText = "Cancelar",
        confirmVariant = "danger",
        onConfirm = () => { },
    } = options || {};

    const modal = document.getElementById("modal-confirm");
    const titleEl = document.getElementById("confirm-title");
    const msgEl = document.getElementById("confirm-message");
    const btnOk = document.getElementById("confirm-ok");
    const btnCancel = document.getElementById("confirm-cancel");

    if (!modal || !titleEl || !msgEl || !btnOk || !btnCancel) {
        console.warn("Modal de confirmação não encontrada no DOM.");
        return;
    }

    titleEl.textContent = title;
    msgEl.textContent = message;
    btnOk.textContent = confirmText;
    btnCancel.textContent = cancelText;

    if (confirmVariant === "danger") {
        btnOk.className =
            "px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-semibold hover:bg-red-700 shadow-sm";
    } else {
        btnOk.className =
            "px-4 py-2 rounded-lg bg-blue-600 text-white text-sm font-semibold hover:bg-blue-700 shadow-sm";
    }

    const close = () => {
        modal.classList.remove("flex");
        btnOk.onclick = null;
        btnCancel.onclick = null;
    };

    btnCancel.onclick = () => close();

    btnOk.onclick = () => {
        close();
        onConfirm();
    };

    modal.classList.add("flex");
}

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    carregarPerfil();
    carregarLogs();

    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) btnLogout.onclick = logout;

    const btnDelete = document.getElementById('btn-delete-hospital');
    if (btnDelete) {
        btnDelete.onclick = (e) => {
            e.preventDefault();

            confirmDialog({
                title: "ATENÇÃO",
                message: "Isso apagará permanentemente sua conta de hospital e todos os dados associados a ela. Deseja continuar?",
                confirmText: "Sim, apagar conta",
                cancelText: "Cancelar",
                confirmVariant: "danger",
                onConfirm: async () => {
                    try {
                        await apiFetch('/hospital/me', { method: 'DELETE' });
                        showToast('Conta excluída com sucesso.', 'success');
                        setTimeout(() => logout(), 1500);
                    } catch (err) {
                        showToast('Erro ao excluir conta: ' + err.message, 'error');
                    }
                }
            });
        };
    }

    const btnUnlock = document.getElementById('btn-unlock-profile');
    if (btnUnlock) {
        btnUnlock.onclick = function () {
            const inputs = document.querySelectorAll('#form-perfil input:not([readonly])');
            const btnSave = document.getElementById('btn-save-profile');
            if (!inputs || inputs.length === 0 || !btnSave) return;

            const isDisabled = inputs[0].disabled;

            inputs.forEach(i => i.disabled = !isDisabled);
            btnSave.classList.toggle('hidden');
            this.textContent = isDisabled ? "Cancelar" : "Editar";

            if (!isDisabled) carregarPerfil();
        };
    }

    const formPerfil = document.getElementById('form-perfil');
    if (formPerfil) {
        formPerfil.onsubmit = async (e) => {
            e.preventDefault();

            const data = {
                nomeFantasia: document.getElementById('nome').value,
                telefoneHospital: document.getElementById('telefone').value,
                endereco: document.getElementById('endereco').value,
                veterinarioResponsavel: document.getElementById('vet').value,
                crmvVeterinario: document.getElementById('crmv').value,
                classificacaoServico: parseInt(document.getElementById('classificacao').value, 10)
            };

            try {
                await apiFetch('/hospital/me', { method: 'PUT', body: JSON.stringify(data) });
                showToast('Perfil atualizado com sucesso!', 'success');
                document.getElementById('btn-unlock-profile')?.click();
            } catch (err) {
                showToast(`Erro ao salvar: ${err.message}`, 'error');
            }
        };
    }
});

// PERFIL
async function carregarPerfil() {
    try {
        const h = await apiFetch('/hospital/me');
        if (!h) return;

        document.getElementById('nome').value = h.nomeFantasia ?? '';
        document.getElementById('cnpj').value = h.cnpj ?? '';
        document.getElementById('email').value = h.emailHospital ?? '';
        document.getElementById('telefone').value = h.telefoneHospital ?? '';
        document.getElementById('endereco').value = h.endereco ?? '';
        document.getElementById('vet').value = h.veterinarioResponsavel ?? '';
        document.getElementById('crmv').value = h.crmvVeterinario ?? '';
        document.getElementById('classificacao').value = h.classificacaoServico ?? '';

        const nomeNav = localStorage.getItem('petone_user_nome');
        if (nomeNav) {
            const navEl = document.getElementById('user-nome-nav');
            if (navEl) navEl.textContent = nomeNav;
        }
    } catch (e) {
        console.error("Erro ao carregar perfil:", e);
    }
}

// LOGS
async function carregarLogs() {
    if (isUpdating) return;
    isUpdating = true;

    const icon = document.getElementById('icon-refresh');
    if (icon) icon.classList.add('animate-spin');

    const div = document.getElementById('lista-logs');

    try {
        const logs = await apiFetch('/hospital/logs');

        if (!div) return;

        if (!logs || logs.length === 0) {
            div.innerHTML = '<p class="text-center py-10 text-gray-400">Nenhum chamado.</p>';
            return;
        }

        div.innerHTML = '';

        logs.sort((a, b) => {
            if (a.status === 'Finalizado' && b.status !== 'Finalizado') return 1;
            if (a.status !== 'Finalizado' && b.status === 'Finalizado') return -1;
            return new Date(b.dataHoraRegistro) - new Date(a.dataHoraRegistro);
        });

        logs.forEach(l => {
            const isFinalizado = l.status === 'Finalizado';
            const isEmAtendimento = l.status === 'Em Atendimento';
            const dateStr = l.dataHoraRegistro
                ? new Date(l.dataHoraRegistro).toLocaleString('pt-BR', {
                    day: '2-digit',
                    month: '2-digit',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                })
                : '--/--/---- --:--';

            let statusColor = 'bg-red-100 text-red-800 animate-pulse';
            let cardBorder = 'border-red-200 border-l-red-500';
            let bgCard = 'bg-red-50';

            if (isEmAtendimento) {
                statusColor = 'bg-yellow-100 text-yellow-800';
                cardBorder = 'border-yellow-200 border-l-yellow-500';
                bgCard = 'bg-yellow-50';
            } else if (isFinalizado) {
                statusColor = 'bg-green-100 text-green-800';
                cardBorder = 'border-gray-200';
                bgCard = 'bg-white opacity-75';
            }

            const card = document.createElement('div');
            card.className = `p-5 rounded-xl border transition relative overflow-hidden shadow-sm border-l-4 ${cardBorder} ${bgCard}`;

            const token = l.tokenEmergencia ?? '';
            const tipo = l.tipoEmergencia ?? '';
            const nomeAnimal = l.nomeAnimal ?? '';
            const especieAnimal = l.especieAnimal ?? '';
            const tutor = l.nomeCompletoTutor ?? '';
            const telefoneTutor = l.telefoneTutor ?? '';
            const relatorioAtual = l.relatorio ?? '';

            card.innerHTML = `
                <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                    <div class="flex-1">
                        <div class="flex items-center gap-3 mb-2">
                            <span class="font-mono font-bold text-gray-700 text-lg bg-white border px-3 py-1 rounded shadow-sm">${token}</span>
                            <span class="text-xs px-2 py-1 rounded-full font-bold uppercase tracking-wide ${statusColor}">${l.status ?? ''}</span>
                            <span class="text-xs text-gray-500 ml-auto md:ml-0">Chegada: ${dateStr}</span>
                        </div>

                        <h4 class="font-bold text-gray-800 text-lg flex items-center gap-2">${tipo}</h4>

                        <div class="mt-2 text-sm text-gray-600">
                            <p><strong>Paciente:</strong> ${nomeAnimal} (${especieAnimal})</p>
                            <p><strong>Tutor:</strong> ${tutor} - ${telefoneTutor}</p>
                        </div>

                        ${relatorioAtual ? `<p class="mt-2 text-xs text-gray-500 bg-white/50 p-2 rounded"><strong>Último registro:</strong> ${relatorioAtual}</p>` : ''}
                    </div>

                    <div class="w-full md:w-auto mt-2 md:mt-0 text-right" id="acoes"></div>
                </div>
            `;

            const acoes = card.querySelector('#acoes');

            if (acoes) {
                if (!isFinalizado) {
                    const btn = document.createElement('button');
                    btn.type = 'button';
                    btn.className = "w-full md:w-auto bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 shadow-sm text-sm font-bold uppercase tracking-wide flex items-center justify-center gap-2 transition transform hover:scale-105";
                    btn.textContent = isEmAtendimento ? 'Continuar' : 'Atender';
                    btn.addEventListener('click', () => abrirFinalizar(token, relatorioAtual));
                    acoes.appendChild(btn);
                } else {
                    const done = document.createElement('div');
                    done.className = "text-green-600 font-medium text-sm";
                    done.textContent = "Concluído";
                    acoes.appendChild(done);
                }
            }

            div.appendChild(card);
        });

        if (window.lucide) lucide.createIcons();
    } finally {
        isUpdating = false;
        if (icon) icon.classList.remove('animate-spin');
    }
}

window.carregarLogs = carregarLogs;

// MODAL
window.abrirFinalizar = (token, relatorioAtual = '') => {
    const tokenInput = document.getElementById('atendimento-token-input');
    const modalToken = document.getElementById('modal-token');
    const relatorio = document.getElementById('relatorio');

    if (tokenInput) tokenInput.value = token;
    if (modalToken) modalToken.innerText = token;
    if (relatorio) relatorio.value = relatorioAtual;

    const vetPerfil = document.getElementById('vet')?.value;
    const crmvPerfil = document.getElementById('crmv')?.value;

    if (vetPerfil) document.getElementById('finalizar-vet').value = vetPerfil;
    if (crmvPerfil) document.getElementById('finalizar-crmv').value = crmvPerfil;

    document.getElementById('modal-finalizar').style.display = 'flex';
};

window.enviarAtendimento = async (acao) => {
    const token = document.getElementById('atendimento-token-input')?.value;
    const relatorio = document.getElementById('relatorio')?.value;
    const prescricao = document.getElementById('prescricao')?.value;
    const vet = document.getElementById('finalizar-vet')?.value;
    const crmv = document.getElementById('finalizar-crmv')?.value;

    if (!token) {
        showToast("Token não encontrado.", "error");
        return;
    }

    if (!relatorio || !vet || !crmv) {
        showToast("Preencha o Relatório, Vet. Responsável e CRMV.", "error");
        return;
    }

    const endpoint = acao === 'finalizar'
        ? `/emergencia/finalizar/${token}`
        : `/emergencia/atualizar/${token}`;

    const data = {
        relatorio,
        prescricao,
        veterinarioResponsavel: vet,
        veterinarioResponsavelFinalizacao: vet,
        crmvVeterinario: crmv,
        crmvVeterinarioFinalizacao: crmv
    };

    try {
        await apiFetch(endpoint, { method: 'PUT', body: JSON.stringify(data) });

        document.getElementById('modal-finalizar').style.display = 'none';

        showToast(
            acao === 'finalizar' ? 'Atendimento finalizado!' : 'Status atualizado: Em Atendimento',
            'success'
        );

        carregarLogs();
    } catch (e) {
        showToast("Erro ao salvar: " + e.message, "error");
    }
};
