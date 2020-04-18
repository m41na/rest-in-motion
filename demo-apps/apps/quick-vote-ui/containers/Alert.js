import { connect } from 'react-redux';
import Alert from '../components/Alert';
import {showAlertAction, hideAlertAction} from '../actions/home';

const mapStateToProps = state => ({
    alert: state.home.alert,
});

const mapDispatchToProps = dispatch => {
    return {
        showAlert: alert => dispatch(showAlertAction(alert)),
        hideAlert: () => dispatch(hideAlertAction()),
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(Alert);