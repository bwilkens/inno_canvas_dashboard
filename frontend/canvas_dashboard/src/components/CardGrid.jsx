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
        <div className='grid-container-wrapper'>
            {/* Active cards */}
            <div className="grid-container">
                {activeCards.map((item, index) => (
                    <Link
                        to={`/dashboard/${item.instanceName}`}
                        key={index}
                        className="card-link"
                    >
                        <div className="card">
                            <h3 className='card-h3'>{item.courseName}</h3>
                            <p>Start datum: {item.startDate}</p>
                            <p>Eind datum: {item.endDate}</p>
                            <p>Cursus code: {item.courseCode}</p>
                            <p className='card-final-p'>Rol: {item.roleInCourse}</p>
                        </div>
                    </Link>
                ))}
            </div>

            {/* Non-active cards, only render if there are any */}
            {nonActiveCards.length > 0 && <hr className="separator-line" />}

            {nonActiveCards.length > 0 && (
                <div className="grid-container">
                    {nonActiveCards.map((item, index) => (
                        <Link
                            to={`/dashboard/${item.instanceName}`}
                            key={index}
                            className="card-link"
                        >
                            <div className="card outdated">
                                <h3 className='card-h3'>{item.courseName}</h3>
                                <p>Start datum: {item.startDate}</p>
                                <p>Eind datum: {item.endDate}</p>
                                <p>Cursus code: {item.courseCode}</p>
                                <p className='card-final-p'>Rol: {item.roleInCourse}</p>
                            </div>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
};

export default CardGrid;
