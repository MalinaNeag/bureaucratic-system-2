import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import WelcomePage from './pages/WelcomePage';
import UsersPage from './pages/UsersPage';
import BooksPage from './pages/BooksPage';
import RevenuePage from './pages/RevenuePage';
const App = () => (
    <Router>
        <Routes>
            <Route path="/" element={<WelcomePage />} />
            <Route path="/users" element={<UsersPage />} />
            <Route path="/books" element={<BooksPage />} />
            <Route path="/revenue" element={<RevenuePage />} />
        </Routes>
    </Router>
);

export default App;