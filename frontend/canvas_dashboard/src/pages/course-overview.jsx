import { useState, useEffect } from 'react';
import { getUserData } from '../api/getUserData.js';
import CardsGrid from '../components/CardGrid';
import UserInfo from '../components/UserInformation';
import useAuthCheck from '../hooks/useAuthCheck';

const CourseOverview = () => {
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

    if (loading) {
        return <div>Loading...</div>;
    }
    if (error) {
        return <div>Error loading user data.</div>;
    }

    return (
        <div>
            <h1>Cursus Overzicht</h1>
            <UserInfo data={userData} />
            <CardsGrid courses={userData.courses} />
        </div>
    );
};

export default CourseOverview;
