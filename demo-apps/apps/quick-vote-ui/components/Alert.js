const Alert = ({ alert: { type, message, dismissable, timeout }, hideAlert }) => {
    console.log(type, message, dismissable, timeout)
    if (!dismissable) {
        setTimeout(function () {
            hideAlert();
        }, timeout || 3000);
    }

    const alertType = () => {
        let className = "";
        switch (type) {
            case 'success':
                className = "alert alert-success";
                break;
            case 'info':
                className = "alert alert-info";
                break;
            case 'warning':
                className = "alert alert-warning";
                break;
            case 'error':
                className = "alert alert-danger";
                break;
            case 'secondary':
                className = "alert alert-secondary";
                break;
            default:
                className = "alert alert-primary";
                break;
        }
        return className.concat(dismissable ? " alert-dismissible fade show" : "");
    }

    return (
        <div className={alertType()} role="alert">
            {message}
            {dismissable ? (
                <button type="button" className="close" data-dismiss="alert" aria-label="Close" onClick={hideAlert}>
                    <span aria-hidden="true"><i className="fas fa-times-circle"></i></span>
                </button>
            ) : null}
        </div>
    );
}

export default Alert;