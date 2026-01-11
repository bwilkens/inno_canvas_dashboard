import { useNavigate } from 'react-router-dom';
import CardButton from '../components/CardButton';
import '../css/home-page.css';

const HomePage = ({ userRole }) => {
    const navigate = useNavigate();

    return (
        <div className="home-container">
            <div className="home-background"></div>
            <div className="home-content-wrapper">
                <h1>Home page</h1>
                <div className="home-card-wrapper">
                    <CardButton
                        cardHeading="Cursus Overzicht"
                        cardText="Hier vind je een overzicht van je huidige en in het verleden gevolgde cursussen."
                        buttonText="Ga naar Cursus Overzicht"
                        buttonAriaLabel="Navigeer naar Cursus Overzicht pagina"
                        buttonOnClick={() => navigate('/course-overview')}
                    />
                    {(userRole === 'ADMIN' || userRole === 'SUPERADMIN') && (
                        <CardButton
                            cardHeading="Admin Portaal"
                            cardText="Vernieuw gegevens van de applicatie en beheer gebruikers via het admin portaal."
                            buttonText="Ga naar Admin Portaal"
                            buttonAriaLabel="Navigeer naar Admin Portaal pagina"
                            buttonOnClick={() => navigate('/admin-portal')}
                        />
                    )}
                </div>
            </div>
        </div>
    );
};

export default HomePage;
