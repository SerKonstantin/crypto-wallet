# Crypto Wallet

[![Actions Status](https://github.com/SerKonstantin/crypto-wallet/actions/workflows/build.yml/badge.svg)](https://github.com/SerKonstantin/crypto-wallet/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/79a90bbdcc37973693d5/maintainability)](https://codeclimate.com/github/SerKonstantin/crypto-wallet/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/79a90bbdcc37973693d5/test_coverage)](https://codeclimate.com/github/SerKonstantin/crypto-wallet/test_coverage)

A Personal Sandbox Project for managing Ethereum wallets. This web application allows users to create or import multiple wallets, with secure transaction processing. Currently, the app supports Ethereum transactions only.

For demonstration, it operates on the Sepolia testnet, enabling safe exploration of wallet management features without the risk of using real assets. Try it out!

Work in progress, so not deployed online yet.

## Technologies used
- Server side - Java, Spring, Web3j, MapStruct
- Client side - React, JavaScript, Web3
- DB -  PostgreSQL, H2, JPA
- Testing - JUnit5, Jest, JaCoCo, Postman
- Planned Deployment - Github Workflows CI, Docker, Sentry, Swagger

## To run app locally
1. **Register for an Infura API Key:**  
Sign up on the [Infura's official website](https://www.infura.io/) and create a personal API key for the Sepolia network.

2. **Set Up the API Key:**  
In the backend folder, create a .env file and add your Infura API key in the following format:
```
INFURA_API_KEY=your_key_here
```

3. **Install Dependencies and start the Server:**
```shell
make install
# then
make run
```

4. **Start the Client:**
```shell
npm start
```
The app will be available at [http://localhost:8080](http://localhost:8080)

If that's too much of a hassle, feel free to check back for the MVP deployment in the near future!

