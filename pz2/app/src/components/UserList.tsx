import { useState } from 'react'
import styles from './UserList.module.css'

type Group = 'ca' | 'regular' | 'other'

interface User {
  id: number
  name: string
  email: string
  group: Group
}

const GROUP_LABELS: Record<Group, string> = {
  ca:      'ЦА',
  regular: 'Постійний клієнт',
  other:   'Інший',
}

const INITIAL_USERS: User[] = [
  { id: 1, name: 'Артем Чуваєв',    email: 'artem@example.com',   group: 'ca' },
  { id: 2, name: 'Марія Іванова',   email: 'maria@example.com',   group: 'regular' },
  { id: 3, name: 'Дмитро Коваль',   email: 'dmytro@example.com',  group: 'ca' },
  { id: 4, name: 'Олена Бондар',    email: 'olena@example.com',   group: 'other' },
  { id: 5, name: 'Сергій Мельник',  email: 'sergiy@example.com',  group: 'regular' },
  { id: 6, name: 'Наталія Шевченко',email: 'natalia@example.com', group: 'regular' },
]

const GROUPS: Group[] = ['ca', 'regular', 'other']

export function UserList() {
  const [users, setUsers] = useState<User[]>(INITIAL_USERS)
  const [filter, setFilter] = useState<Group | 'all'>('all')

  const visible = filter === 'all' ? users : users.filter(u => u.group === filter)

  function changeGroup(id: number, group: Group) {
    setUsers(prev => prev.map(u => u.id === id ? { ...u, group } : u))
  }

  return (
    <section className={styles.section}>
      <h2 className={styles.title}>Список користувачів</h2>

      <div className={styles.filters}>
        <button
          className={`${styles.filterBtn} ${filter === 'all' ? styles.active : ''}`}
          onClick={() => setFilter('all')}
        >
          Всі <span className={styles.count}>{users.length}</span>
        </button>
        {GROUPS.map(g => (
          <button
            key={g}
            className={`${styles.filterBtn} ${styles[g]} ${filter === g ? styles.active : ''}`}
            onClick={() => setFilter(g)}
          >
            {GROUP_LABELS[g]}
            <span className={styles.count}>{users.filter(u => u.group === g).length}</span>
          </button>
        ))}
      </div>

      <div className={styles.list}>
        {visible.map(user => (
          <div key={user.id} className={styles.row}>
            <div className={styles.avatar}>
              {user.name.charAt(0)}
            </div>
            <div className={styles.info}>
              <span className={styles.name}>{user.name}</span>
              <span className={styles.email}>{user.email}</span>
            </div>
            <div className={styles.groupSelect}>
              {GROUPS.map(g => (
                <button
                  key={g}
                  className={`${styles.tag} ${styles[`tag_${g}`]} ${user.group === g ? styles.tagActive : ''}`}
                  onClick={() => changeGroup(user.id, g)}
                  title={GROUP_LABELS[g]}
                >
                  {GROUP_LABELS[g]}
                </button>
              ))}
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}
