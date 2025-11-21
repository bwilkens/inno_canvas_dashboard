import React, { useEffect, useState } from "react";
import useAuthCheck from "../hooks/useAuthCheck.jsx";

export default function TokenPage() {
    useAuthCheck();

    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch("/api/v1/user/token", {
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to fetch token");
                return res.text();
            })
            .then(token => {
                setToken(token);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setToken(null);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Loading token...</p>;

    return (
        <div style={{ padding: "20px" }}>
            <h1>Your Access Token</h1>

            {token ? (
                <pre
                    style={{
                        padding: "10px",
                        backgroundColor: "#f4f4f4",
                        borderRadius: "6px",
                        width: "100%",
                        overflowX: "auto"
                    }}
                >
          {token}
        </pre>
            ) : (
                <p style={{ color: "red" }}>Could not load token — are you logged in?</p>
            )}
        </div>
    );
}
