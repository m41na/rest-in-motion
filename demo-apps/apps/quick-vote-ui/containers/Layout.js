import { connect } from 'react-redux';
import Layout from '../components/Layout';
import {changePageAction} from '../actions/home';

const mapStateToProps = state => ({
    headerText: state.staticText.Header,
    home: state.home
});

const mapDispatchToProps = dispatch => {
    return {
        changePage: page => dispatch(changePageAction(page)),
    }
};

export default connect(
    mapStateToProps,
    mapDispatchToProps,
)(Layout);