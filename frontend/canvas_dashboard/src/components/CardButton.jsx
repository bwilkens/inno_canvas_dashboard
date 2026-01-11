import '../css/card-button.css';

const CardButton = ({ cardHeading, cardText, buttonText, buttonAriaLabel, buttonOnClick }) => (
    <div className="card-content-wrapper">
        <div className="card-content">
            <h2>{cardHeading}</h2>
            <div className="card-body">
                <p>{cardText}</p>
                <div className="card-button-wrapper">
                    <button
                        className="card-button"
                        aria-label={buttonAriaLabel || buttonText}
                        onClick={buttonOnClick}
                        type="button"
                    >
                        {buttonText}
                    </button>
                </div>
            </div>
        </div>
    </div>
);

export default CardButton;
