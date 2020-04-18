import Layout from '../containers/Layout';
import Page from '../containers/Page';

import { Provider } from 'react-redux';
import initialState from '../store/state';
import createStore from '../store';

const store = createStore(initialState);
console.log(store.getState());

export default function Index() {
  return (
    <Provider store={store}>
      <Layout>
        <Page />
      </Layout>
    </Provider>
  );
}