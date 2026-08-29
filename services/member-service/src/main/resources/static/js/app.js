function formatPhone(value) {
    const digits = value.replace(/\D/g, '').slice(0, 11);
    if (digits.length <= 3) return digits;
    if (digits.length <= 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
    if (digits.length === 10) return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6)}`;
    return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
}

document.querySelectorAll('.phone-input').forEach(input => {
    input.value = formatPhone(input.value);
    input.addEventListener('input', () => {
        input.value = formatPhone(input.value);
    });
});

const passwordModal = document.querySelector('#password-modal');
const logoutModal = document.querySelector('#logout-modal');
function setPasswordModal(open) {
    if (!passwordModal) return;
    passwordModal.classList.toggle('open', open);
    passwordModal.setAttribute('aria-hidden', String(!open));
}

document.querySelectorAll('[data-open-password-modal]').forEach(button =>
    button.addEventListener('click', () => {
        button.closest('details')?.removeAttribute('open');
        setPasswordModal(true);
    }));
document.querySelectorAll('[data-close-password-modal]').forEach(button =>
    button.addEventListener('click', () => setPasswordModal(false)));
passwordModal?.addEventListener('click', event => {
    if (event.target === passwordModal) setPasswordModal(false);
});
document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
        setPasswordModal(false);
        setLogoutModal(false);
    }
});

function setLogoutModal(open) {
    if (!logoutModal) return;
    logoutModal.classList.toggle('open', open);
    logoutModal.setAttribute('aria-hidden', String(!open));
}

document.querySelectorAll('[data-open-logout-modal]').forEach(button =>
    button.addEventListener('click', () => {
        button.closest('details')?.removeAttribute('open');
        setLogoutModal(true);
    }));
document.querySelectorAll('[data-close-logout-modal]').forEach(button =>
    button.addEventListener('click', () => setLogoutModal(false)));
logoutModal?.addEventListener('click', event => {
    if (event.target === logoutModal) setLogoutModal(false);
});

document.addEventListener('click', event => {
    document.querySelectorAll('.account-menu[open]').forEach(menu => {
        if (!menu.contains(event.target)) menu.removeAttribute('open');
    });
});
