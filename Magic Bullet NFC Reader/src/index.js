import React from 'react'
import ReactDOM from 'react-dom'
import MyForm from './components/MyForm'
import { Router, Route, browserHistory } from 'react-router'
import { ApolloProvider, ApolloClient, createNetworkInterface } from 'react-apollo'
import { SubscriptionClient, addGraphQLSubscriptions } from 'subscriptions-transport-ws'
import 'tachyons'
import './index.css'

const wsClient = new SubscriptionClient('wss://subscriptions.us-west-2.graph.cool/v1/cjmo44byo7sds0173t8f5fg9m', {
  reconnect: true,
  timeout: 20000
})

const networkInterface = createNetworkInterface({
  uri: 'https://api.graph.cool/simple/v1/cjmo44byo7sds0173t8f5fg9m',
})

const networkInterfaceWithSubscriptions = addGraphQLSubscriptions(
  networkInterface,
  wsClient
)

const client = new ApolloClient({
  networkInterface: networkInterfaceWithSubscriptions,
  dataIdFromObject: o => o.id
})

ReactDOM.render((
  <ApolloProvider client={client}>
    <Router history={browserHistory}>
      <Route path='/' component={MyForm} />
    </Router>
  </ApolloProvider>
  ),
  document.getElementById('root')
)
