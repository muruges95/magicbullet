import React from 'react'

import { graphql, gql } from 'react-apollo'
import { Button, Checkbox, Form, Header, Icon } from 'semantic-ui-react'

class MyForm extends React.Component {
  constructor(props){
    super(props)
    this.state = {
      lastName: ""
    }
  }
  static propTypes = {
    data: React.PropTypes.object,
  }

  componentWillReceiveProps(newProps) {
    if (!newProps.data.loading) {
      if (this.subscription) {
        if (newProps.data.allPosts !== this.props.data.allPosts) {
          //
          // let defaultState = {
          //   firstName: "Johnny",
          //   lastName: "Tan",
          //   zip: "94025",
          //   city: "Menlo Park",
          //   state: "CA",
          //   doe: "12/13/2019",
          //   doi: "13/13/2014",
          //   address1: "1226 University Drive",
          //   address2: "Windows",
          //   ssn: "1233211234",
          // }
          //
          // this.setState(defaultState)

          console.log("Received", newProps.data.allPosts[0])
          this.setState(newProps.data.allPosts[0])

          this.subscription()
        } else {
          return
        }
      }
      this.subscription = newProps.data.subscribeToMore({
        document: gql`
          subscription {
            Post(filter: {
              mutation_in: [CREATED]
            }) {
              node {
                id
                address1
                address2
                city
                doe
                doi
                firstName
                id
                lastName
                ssn
                state
                zip
              }
            }
          }
        `,
        variables: null,

        // this is where the magic happens
        updateQuery: (previousState, {subscriptionData}) => {
          const newPost = subscriptionData.data.Post.node

          return {
            allPosts: [
              {
                ...newPost
              },
              ...previousState.allPosts
            ]
          }
        },
        onError: (err) => console.error(err),
      })
    }
  }

  handleChange = (e) => {
    let x = {[e.target.name]: e.target.value}
    console.log(x)
    this.setState({[e.target.name]: e.target.value})
  }

  resetState (e){
    e.preventDefault()
    let defaultState = {
      firstName: "",
      lastName: "",
      zip: "",
      city: "",
      state: "",
      doe: "",
      doi: "",
      address1: "",
      address2: "",
      ssn: "",
    }
    this.setState(defaultState)
  }
  render () {
    if (this.props.data.loading) {
      return (<div>Loading</div>)
    }
    return (
      <div className='w-100 flex justify-center'>
        <div>
          <div style={{width:"80%", marginLeft:"10%", marginTop:"3%"}}>
            <Header as='h2' icon textAlign='center'>
              <Icon name='car' />
              DMV Renewal Form
              <Header.Subheader>Enter information that you repeatedly type across many other forms</Header.Subheader>
            </Header>
            <Form>
              <Form.Group widths='equal'>
                <Form.Field>
                  <label>First Name</label>
                  <input placeholder='First Name' name="firstName" value={this.state.firstName} onChange={this.handleChange}/>
                </Form.Field>
                <Form.Field>
                  <label>Last Name</label>
                  <input placeholder='Last Name' name="lastName" value={this.state.lastName} onChange={this.handleChange}/>
                </Form.Field>
              </Form.Group>

              <Form.Group widths='equal'>
                <Form.Field>
                  <label>Date of Issue</label>
                  <input placeholder='Date of Issue' name="doi" value={this.state.doi} onChange={this.handleChange}/>
                </Form.Field>
                <Form.Field>
                  <label>Date of Expiry</label>
                  <input name="doe" placeholder='Last Name' value={this.state.doe} onChange={this.handleChange}/>
                </Form.Field>
              </Form.Group>

              <Form.Field>
                <label>Address Line 1</label>
                <input placeholder='Address Line 1' name="address1" value={this.state.address1} onChange={this.handleChange}/>
              </Form.Field>
              <Form.Field>
                <label>Address Line 2</label>
                <input placeholder='Address Line 2' name="address2" value={this.state.address2} onChange={this.handleChange}/>
              </Form.Field>
              <Form.Group>
                <Form.Field>
                  <label>City</label>
                  <input placeholder='City' name="city" value={this.state.city} onChange={this.handleChange}/>
                </Form.Field>
                <Form.Field>
                  <label>State</label>
                  <input placeholder='State' name="state" value={this.state.state} onChange={this.handleChange}/>
                </Form.Field>
                <Form.Field>
                  <label>Zip</label>
                  <input placeholder='Zip' name="zip" value={this.state.zip} onChange={this.handleChange}/>
                </Form.Field>
              </Form.Group>
              <Form.Field>
                <label>SSN</label>
                <input placeholder='SSN' name="ssn" value={this.state.ssn} onChange={this.handleChange}/>
              </Form.Field>
              <Form.Group>
                <Form.Field>
                  <Checkbox label='I agree to the Terms and Conditions' />
                </Form.Field>
                <Button type='submit'>Submit</Button>
                <Button onClick={this.resetState.bind(this)}>Reset</Button>
              </Form.Group>
            </Form>
          </div>
        </div>
      </div>
    )
  }
}

const FeedQuery = gql`query allPosts {
  allPosts {
    id
    address1
    address2
    city
    doe
    doi
    firstName
    id
    lastName
    ssn
    state
    zip
  }
}`

const MyFormWithData = graphql(FeedQuery, {
  options: {
    forcePolicy: 'cache-and-network'
  }
})(MyForm)

export default MyFormWithData


