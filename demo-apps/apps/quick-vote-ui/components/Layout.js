import Head from 'next/head'
import Header from './Header';

const Layout = props => (
  <>
    <Head>
      <title>Quick Vote</title>
      <meta charSet="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no" />
      <meta name="description" content="" />
      <meta name="theme-color" content="#563d7c" />
      <link href="css/bootstrap/bootstrap.min.css" rel="stylesheet" />
      <link href="css/font-awesome/fontawesome.min.css" rel="stylesheet"></link>
      <link href="css/font-awesome/brands.min.css" rel="stylesheet"></link>
      <link href="css/font-awesome/solid.min.css" rel="stylesheet"></link>
      <link href="css/style.css" rel="stylesheet" />
    </Head>
    <Header home={props.home} changePage={props.changePage} />
    <main role="main" className="flex-shrink-0">
      <div className="container">
        {props.children}
      </div>
    </main>

    <footer className="footer mt-auto py-3">
      <div className="container text-center">
        <span className="text-muted">Quick Vote <i className="fas fa-balance-scale"></i> - Practical Dime &copy; 2020.</span>
      </div>
    </footer>

    <script src="js/jquery-3.4.1.slim.min.js" type="text/javascript"></script>
    <script src="js/popper.min.js" type="text/javascript"></script>
    <script src="js/bootstrap/bootstrap.min.js"></script>
  </>
);

export default Layout;