import { useState, useEffect } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { getUserData } from '../api/getUserData.js';
import { toast } from 'react-toastify';
import { updateDatabase } from '../api/updateDatabase.js';
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

    function handleGenerateResult() {
        fetch('', {
            method: 'POST',
            credentials: 'include',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Generate result failed');
                }
                return response.json();
            })
            .catch((error) => {
                toast.error(error.message);
            });
    }
    function handleGenerateCourse() {
        fetch('', {
            method: 'POST',
            credentials: 'include',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('Generate course failed');
                }
                return response.json();
            })
            .catch((error) => {
                toast.error(error.message);
            });
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
                            cardHeading="Genereer Resultaat"
                            cardText="TODO"
                            buttonText="Genereer Resultaat"
                            buttonAriaLabel="Genereer Resultaat"
                            buttonOnClick={handleGenerateResult}
                        ></CardButton>

                        <CardButton
                            cardHeading="Genereer Cursus"
                            cardText="TODO"
                            buttonText="Genereer Cursus"
                            buttonAriaLabel="Genereer Cursus"
                            buttonOnClick={handleGenerateCourse}
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
