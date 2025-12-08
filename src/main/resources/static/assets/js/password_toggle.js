document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.toggle-password').forEach((btn) => {
    const targetId = btn.getAttribute('data-target');
    const input = document.getElementById(targetId);
    if (!input) return;

    const visivelInicial = input.type === 'text';
    btn.setAttribute('aria-pressed', String(visivelInicial));

    btn.addEventListener('click', () => {
      const isPassword = input.type === 'password';

      input.type = isPassword ? 'text' : 'password';

      const agoraVisivel = isPassword;
      btn.setAttribute('aria-pressed', String(agoraVisivel));
    });
  });
});
