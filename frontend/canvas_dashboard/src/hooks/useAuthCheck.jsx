import { useEffect } from "react";

const VITE_AUTH_URL = import.meta.env.VITE_AUTH_URL;

function redirectToLogin() {
    const returnTo = encodeURIComponent(window.location.href);
    fetch("/api/v1/security/redirect", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        credentials: "include",
        body: "url=" + returnTo
    }).finally(() => {
        window.location.href = VITE_AUTH_URL;
    });
}

function useAuthCheck() {
    useEffect(() => {
        fetch("/api/v1/security/users/me", { credentials: "include" })
            .then((response) => {
                if (response.status === 401 || response.status === 403) {
                    redirectToLogin();
                }
            })
            .catch(redirectToLogin);
    }, []);
}

export default useAuthCheck;