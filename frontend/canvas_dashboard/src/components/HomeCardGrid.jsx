import { useNavigate } from 'react-router-dom';
import '../css/home-card-grid.css';

const HomeCardGrid = ({ userRole }) => {
    const navigate = useNavigate();

    return (
        <div className="card-wrapper">
            <div className="card-content-wrapper">
                <div className="card-content">
                    <h2>Cursus Overzicht</h2>
                    <p>Hier vind je een overzicht van je huidige en in het verleden gevolgde cursussen.</p>
                    <button
                        className="card-button"
                        aria-label="Navigeer naar Cursus Overzicht pagina"
                        onClick={() => navigate('/course-overview')}
                        type="button"
                    >
                        Ga naar Cursus Overzicht
                    </button>
                </div>
            </div>
            {(userRole === 'ADMIN' || userRole === 'SUPERADMIN') && (
                <div className="card-content-wrapper">
                    <div className="card-content">
                        <h2>Admin Portaal</h2>
                        <p>Vernieuw gegevens van de applicatie en beheer gebruikers via het admin portaal.</p>
                        <button
                            className="card-button"
                            aria-label="Navigeer naar Admin Portaal pagina"
                            onClick={() => navigate('/admin-portal')}
                            type="button"
                        >
                            Ga naar Admin Portaal
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default HomeCardGrid;
