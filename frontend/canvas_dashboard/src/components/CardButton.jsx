import '../css/card-button.css';

const CardButton = ({ cardHeading, cardText, buttonText, buttonAriaLabel, buttonOnClick }) => (
    <div className="card-content-wrapper">
        <div className="card-content">
            <h2>{cardHeading}</h2>
            <p>{cardText}</p>
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
);

export default CardButton;
