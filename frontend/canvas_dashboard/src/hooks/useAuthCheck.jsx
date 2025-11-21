import { useEffect } from "react";

export default function useAuthCheck() {
    useEffect(() => {
        fetch("/api/v1/user/me", { credentials: "include" })
            .then((res) => {
                if (res.status === 401 || res.status === 403) {
                    const returnTo = encodeURIComponent(window.location.href);

                    fetch("/auth/save-redirect?url=" + returnTo, {
                        credentials: "include",
                    }).finally(() => {
                        window.location.href =
                            "http://localhost:8080/oauth2/authorization/azure"; // TODO: should not be hardcoded
                    });
                }
            })
            .catch(() => {
                const returnTo = encodeURIComponent(window.location.href);

                fetch("/auth/save-redirect?url=" + returnTo, {
                    credentials: "include",
                }).finally(() => {
                    window.location.href =
                        "http://localhost:8080/oauth2/authorization/azure"; // TODO: should not be hardcoded
                });
            });
    }, []);
}
