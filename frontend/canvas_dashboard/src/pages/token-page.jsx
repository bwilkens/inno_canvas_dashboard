import React, { useEffect, useState } from "react";
import useAuthCheck from "../hooks/useAuthCheck.jsx";

function TokenPage() {
    useAuthCheck();

    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);
    const [copied, setCopied] = useState(false);

    useEffect(() => {
        fetch("/api/v1/security/users/me/token", {
            credentials: "include"
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch token");
                return response.text();
            })
            .then(token => {
                setToken(token);
                setLoading(false);
            })
            .catch(error => {
                console.error(error);
                setToken(null);
                setLoading(false);
            });
    }, []);

    const handleCopy = () => {
        navigator.clipboard.writeText(token);
        setCopied(true);
        setTimeout(() => setCopied(false), 500);
    };

    if (loading) return <p>Loading token...</p>;

    return (
        <div style={{ padding: "20px" }}>
            <h1>Your Access Token</h1>

            {token ? (
                <div>
                    <button
                        onClick={handleCopy}
                        style={{
                            marginBottom: "10px",
                            padding: "6px 12px",
                            borderRadius: "4px",
                            cursor: "pointer",
                            backgroundColor: copied ? "#b2f2bb" : ""
                        }}
                    >
                        Copy token
                    </button>
                    <pre
                        style={{
                            padding: "10px",
                            backgroundColor: "#f4f4f4",
                            borderRadius: "6px",
                            width: "100%",
                            overflowX: "auto",
                            color: "black"
                        }}
                    >
                        {token}
                    </pre>
                </div>
            ) : (
                <p style={{ color: "red" }}>Could not load token — are you logged in?</p>
            )}
        </div>
    );
}

export default TokenPage;