import Document, { Html, Head, Main, NextScript } from 'next/document';

class AppDocument extends Document {

    static async getInitialProps(ctx) {
        const initialProps = await Document.getInitialProps(ctx)
        return { ...initialProps }
    }

    render() {
        return (
            <Html lang="en" className="h-100">
                <Head />
                <body className="d-flex flex-column h-100">
                    <Main />
                    <NextScript />
                </body>
            </Html>
        )
    }
}
export default AppDocument;