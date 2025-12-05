import { useState, useEffect } from "react";
import { getUserData } from "../api/getUserData.js";
import AdminActionButton from "../components/AdminActionButton";
import UserInfo from "../components/UserInformation";
import useAuthCheck from "../hooks/useAuthCheck";
import "../css/admin-button-group.css";

const AdminDashboard = () => {
    useAuthCheck();

    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function loadData() {
            try {
                const data = await getUserData();
                setUserData(data);
            } catch (error) {
                setError(error);
            } finally {
                setLoading(false);
            }
        }
        loadData();
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error loading user data.</div>;

    return (
        <div>
            <UserInfo data={userData} />
              <div class="button-group">
                <AdminActionButton name="Genereer Resultaat"></AdminActionButton>
                <AdminActionButton name="Genereer Cursus"></AdminActionButton>
                <AdminActionButton name="Update Cursus" disabled={true}></AdminActionButton>
                <AdminActionButton name="Health"></AdminActionButton>
            </div>
        </div>
    );
};

export default AdminDashboard;