# WellWork

Projeto: WellWork (API REST Spring Boot)
Estrutura básica gerada para começar o desenvolvimento de um serviço de check-ins de bem-estar que integra LLM (Groq via Spring AI), mensageria e security.

### Como rodar (local)
1. Ajuste as variáveis de ambiente em `application.yml` ou exporte no shell.
2. Subir dependências com Docker: `docker compose up -d`
3. `mvn clean package` e rode: `java -jar target/wellwork-0.0.1-SNAPSHOT.jar`

### O que inclui
- Entidades: User, CheckIn, GeneratedMessage
- Repositórios Spring Data JPA
- Serviço e controller de exemplo para criação/listagem de CheckIns
- Skeleton de integração com AI (AiConfig) e SecurityConfig (JWT/OAuth filter placeholder)
- Dockerfile e docker-compose para Postgres + RabbitMQ

---
Este é um esqueleto inicial. Posso preencher qualquer arquivo com mais detalhes (JWT auth, listeners RabbitMQ, integração completa com Spring AI e Groq, testes unitários, i18n, caching configurado) — diga qual parte quer que eu implemente em seguida.
