import CardGridSkeleton from './CardGridSkeleton';
import { Link } from 'react-router-dom';
import '../css/card-grid.css';

const CardGrid = ({ courses }) => {
    if (!courses) {
        return <CardGridSkeleton />;
    }

    const currentDate = new Date();

    const activeCards = courses.filter((item) => {
        const start = new Date(item.startDate);
        const end = new Date(item.endDate);
        return currentDate >= start && currentDate <= end;
    });

    const nonActiveCards = courses.filter((item) => {
        const start = new Date(item.startDate);
        const end = new Date(item.endDate);
        return currentDate < start || currentDate > end;
    });

    return (
        <div>
            {/* Active cards */}
            <div className="grid-container">
                {activeCards.map((item, index) => (
                    <Link
                        to={`/dashboard/${item.instanceName}`}
                        key={index}
                        className="card-link"
                    >
                        <div className="card">
                            <h3>{item.courseName}</h3>
                            <p>Start datum: {item.startDate}</p>
                            <p>Eind datum: {item.endDate}</p>
                            <p>Cursus code: {item.courseCode}</p>
                            <p>Rol: {item.roleInCourse}</p>
                        </div>
                    </Link>
                ))}
            </div>

            {nonActiveCards.length > 0 && <hr className="separator-line" />}

            {/* Non-active cards */}
            <div className="grid-container">
                {nonActiveCards.map((item, index) => (
                    <Link
                        to={`/dashboard/${item.instanceName}`}
                        key={index}
                        className="card-link"
                    >
                        <div className="card outdated">
                            <h3>{item.courseName}</h3>
                            <p>Start datum: {item.startDate}</p>
                            <p>Eind datum: {item.endDate}</p>
                            <p>Cursus Code: {item.instanceName}</p>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default CardGrid;
