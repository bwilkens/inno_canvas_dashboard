import { Link } from "react-router-dom";
import "../css/home-card-grid.css";

const HomeCardGrid = ({ userRole }) => {
  return (
    <div className="card_wrapper">
      <div className="card_content-wrapper">
        <div className="card_content">
          <h2>Cursus Overzicht</h2>
          <p>Hier vind je een overzicht van je huidige en in het verleden gevolgde cursussen.</p>
          <Link to="/course-overview" className="card_button">
            Ga naar Cursus Overzicht
          </Link>
        </div>
      </div>
      {(userRole === "ADMIN" || userRole === "SUPERADMIN") && (
        <div className="card_content-wrapper">
          <div className="card_content">
            <h2>Admin Portaal</h2>
            <p>Vernieuw gegevens van de applicatie en beheer gebruikers via het admin portaal.</p>
            <Link to="/admin-portal" className="card_button">
              Ga naar Admin Portaal
            </Link>
          </div>
        </div>
      )}
    </div>
  );
};

export default HomeCardGrid;