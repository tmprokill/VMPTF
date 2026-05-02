import { UserList } from './components/UserList'
import { ApiUsersSection } from './components/ApiUsersSection'
import './App.css'

function App() {
  return (
    <>
      <header className="page-header">
        <h1>Менеджер користувачів</h1>
        <p className="page-sub">Управління клієнтами та їх групами</p>
      </header>

      <UserList />
      <ApiUsersSection />
    </>
  )
}

export default App
