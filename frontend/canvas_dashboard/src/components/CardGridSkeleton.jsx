import '../css/card-grid.css';
import '../css/skeleton.css';

const CardGridSkeleton = () => {
    const skeletons = Array.from({ length: 8 });

    return (
        <div>
            <div className="grid-container">
                {skeletons.map((_, index) => (
                    <div key={index} className="card skeleton-card">
                        <div className="skeleton skeleton-title"></div>
                        <div className="skeleton skeleton-text"></div>
                        <div className="skeleton skeleton-text"></div>
                        <div className="skeleton skeleton-text"></div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default CardGridSkeleton;
