import { useJsonPlaceholderUsers } from '../hooks/useJsonPlaceholderUsers'
import styles from './ApiUsersSection.module.css'

export function ApiUsersSection() {
  const { data, loading, error } = useJsonPlaceholderUsers()

  return (
    <section className={styles.section}>
      <h2 className={styles.title}>Користувачі з JSONPlaceholder</h2>
      <p className={styles.sub}>Реальні дані з публічного API</p>

      {loading && (
        <div className={styles.state}>
          <span className={styles.spinner} />
          Завантаження…
        </div>
      )}

      {error && (
        <div className={`${styles.state} ${styles.err}`}>Помилка: {error}</div>
      )}

      {!loading && !error && (
        <div className={styles.grid}>
          {data.map(user => (
            <div key={user.id} className={styles.card}>
              <div className={styles.avatar}>
                {user.name.charAt(0).toUpperCase()}
              </div>
              <div className={styles.info}>
                <h3 className={styles.name}>{user.name}</h3>
                <span className={styles.email}>{user.email}</span>
                <div className={styles.meta}>
                  <span className={styles.metaItem}>{user.company.name}</span>
                  <span className={styles.dot}>·</span>
                  <span className={styles.metaItem}>{user.address.city}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  )
}
