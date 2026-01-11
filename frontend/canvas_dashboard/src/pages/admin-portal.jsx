import { useState, useEffect } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { getUserData } from '../api/getUserData.js';
import { toast } from 'react-toastify';
import { refreshCanvasData, refreshDashboards, updateDatabase } from '../api/adminPortalApi.js';
import CardButton from '../components/CardButton';
import UserInfo from '../components/UserInformation';
import useAuthCheck from '../hooks/useAuthCheck';
import '../css/admin-button.css';
import '../css/admin-portal-page.css';
import AdminManagementTable from '../components/AdminManagementTable.jsx';

const AdminDashboard = () => {
    useAuthCheck();
    const navigate = useNavigate();

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

    if (loading) {
        return <div>Loading...</div>;
    }
    if (error) {
        return <div>Error loading user data.</div>;
    }
    if (!userData) {
        return <Navigate to="/" replace />;
    }
    if (userData.appRole !== 'ADMIN' && userData.appRole !== 'SUPERADMIN') {
        return <Navigate to="/" replace />;
    }

    function handleHealth() {
        navigate('/health');
    }

    async function handleRefreshResults() {
        try {
            await refreshCanvasData();
            toast.success('Canvas-gegevens zijn succesvol ververst.');
        } catch (error) {
            toast.error(error.message);
        }
    }

    async function handleRefreshDashboards() {
        try {
            await refreshDashboards();
            toast.success('Dashboards zijn succesvol vernieuwd.');
        } catch (error) {
            toast.error(error.message);
        }
    }

    async function handleRefreshDatabase() {
        try {
            await updateDatabase();
            toast.success('Database is succesvol geüpdatet.');
        } catch (error) {
            toast.error(error.message);
        }
    }

    return (
        <div className='admin-container'>
            <div className='admin-background'></div>
            <div className='admin-content-wrapper'>
                <h1>Admin Portaal</h1>
                <UserInfo data={userData} />

                <div className='admin-card-container'>
                    <h2>Admin Acties</h2>
                    <div className="admin-card-wrapper">
                        <CardButton
                            cardHeading="Vernieuw Resultaten"
                            cardText="Klik hier om handmatig de resultaten te updaten met de laatste gegevens vanuit Canvas."
                            buttonText="Resultaten vernieuwen"
                            buttonAriaLabel="Resultaten vernieuwen"
                            buttonOnClick={handleRefreshResults}
                        ></CardButton>

                        <CardButton
                            cardHeading="Vernieuw Dashboards"
                            cardText="Klik hier om handmatig de dashboard HTML-pagina's te genereren met de laatste resultaten die zijn opgehaald uit Canvas."
                            buttonText="Dashboards vernieuwen"
                            buttonAriaLabel="Dashboards vernieuwen"
                            buttonOnClick={handleRefreshDashboards}
                        ></CardButton>

                        <CardButton
                            cardHeading="Database Updaten"
                            cardText="Klik hier om de database te updaten met de laatste gebruiker- en cursusinformatie die aangeleverd is door de Python applicatie."
                            buttonText="Update database"
                            buttonAriaLabel="Update database"
                            buttonOnClick={handleRefreshDatabase}
                        ></CardButton>

                        <CardButton 
                            cardHeading="Grafana Dashboard"
                            cardText="Klik hier om te navigeren naar het Grafana dashboard en de server te monitoren."
                            buttonText="Ga naar Grafana dashboard"
                            buttonAriaLabel="Ga naar Grafana dashboard"
                            buttonOnClick={handleHealth}
                        ></CardButton>
                    </div>
                </div>
                {userData.appRole === 'SUPERADMIN' && (
                    <AdminManagementTable />
                )}
            </div>
        </div>
    );
};

export default AdminDashboard;
