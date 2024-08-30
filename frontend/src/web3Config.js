import config from './config';

const getWeb3Url = () => {
    const apiKey = process.env.REACT_APP_INFURA_API_KEY;

    if (!apiKey) {
        throw new Error('Infura API key is not provided');
    }

    return config.infuraBaseUrl + apiKey;
};

export default getWeb3Url;