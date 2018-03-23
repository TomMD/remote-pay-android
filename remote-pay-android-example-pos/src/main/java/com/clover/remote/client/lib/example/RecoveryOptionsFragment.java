/*
 * Copyright (C) 2018 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clover.remote.client.lib.example;

import com.clover.remote.PendingPaymentEntry;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.lib.example.model.POSCard;
import com.clover.remote.client.lib.example.model.POSOrder;
import com.clover.remote.client.lib.example.model.POSPayment;
import com.clover.remote.client.lib.example.model.POSStore;
import com.clover.remote.client.lib.example.model.POSTransaction;
import com.clover.remote.client.lib.example.model.StoreObserver;
import com.clover.remote.client.lib.example.utils.CurrencyUtils;
import com.clover.remote.client.messages.RetrievePaymentRequest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.List;


public class RecoveryOptionsFragment extends Fragment implements EnterPaymentIdFragment.EnterPaymentIdFragmentListener{
  private POSStore store;
  private WeakReference<ICloverConnector> cloverConnectorWeakReference;
  private View view;
  private Button reset, paymentById, pendingPayments, deviceStatus, deviceStatusResend;

  public static RecoveryOptionsFragment newInstance(POSStore store, ICloverConnector cloverConnector) {
    RecoveryOptionsFragment fragment = new RecoveryOptionsFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    fragment.setStore(store);
    fragment.setCloverConnector(cloverConnector);
    return fragment;
  }

  public RecoveryOptionsFragment(){

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_recovery_options, container, false);
    reset = (Button) view.findViewById(R.id.ResetDeviceButton);
    paymentById = (Button) view.findViewById(R.id.PaymentByIdButton);
    pendingPayments = (Button) view.findViewById(R.id.PendingPaymentsButton);
    deviceStatus = (Button) view.findViewById(R.id.DeviceStatusButton);
    deviceStatusResend = (Button) view.findViewById(R.id.DeviceStatusResendButton);

    store.addStoreObserver(new StoreObserver() {
      @Override
      public void onCurrentOrderChanged(POSOrder currentOrder) {

      }

      @Override public void newOrderCreated(POSOrder order, boolean userInitiated) {

      }

      @Override public void cardAdded(POSCard card) {

      }

      @Override public void refundAdded(POSTransaction refund) {

      }

      @Override public void preAuthAdded(POSPayment payment) {

      }

      @Override public void preAuthRemoved(POSPayment payment) {

      }

      @Override public void pendingPaymentsRetrieved(final List<PendingPaymentEntry> pendingPayments) {
        if(getActivity() != null){
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (pendingPayments == null) {
                showPopupMessage("Pending Payments", new String[]{"Get Pending Payments Failed"}, false);
              } else {
                if (pendingPayments.size() > 0) {
                  String[] pending = new String[pendingPayments.size()];
                  int index = 0;
                  for (PendingPaymentEntry pendingPayment : pendingPayments) {
                    pending[index] = (String.format("%-16s%-10s", pendingPayment.paymentId, CurrencyUtils.convertToString(pendingPayment.amount)));
                    index++;
                  }
                  showPopupMessage("Pending Payments", pending, true);
                } else {
                  showPopupMessage("Pending Payments", new String[]{"There are no Pending Payments"}, false);
                }
              }
            }
          });
        }
      }

      @Override
      public void transactionsChanged(List<POSTransaction> transactions) {

      }
    });


    paymentById.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showEnterPaymentIdDialog();
      }
    });
    pendingPayments.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getPendingPayments();
      }
    });

    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  private void getPendingPayments(){
    getCloverConnector().retrievePendingPayments();
  }

  private void showEnterPaymentIdDialog() {
    FragmentManager fm = getFragmentManager();
    EnterPaymentIdFragment enterPaymentIdFragment = EnterPaymentIdFragment.newInstance();
    enterPaymentIdFragment.addListener(this);
    enterPaymentIdFragment.show(fm, "fragment_enter_payment_id");
  }

  private void showPopupMessage (final String title, final String[] content, final boolean monospace) {
    FragmentManager fm = getFragmentManager();
    PopupMessageFragment popupMessageFragment = PopupMessageFragment.newInstance(title, content, monospace);
    popupMessageFragment.show(fm, "fragment_popup_message");
  }

  @Override
  public void onLookup(String paymentId) {
    getCloverConnector().retrievePayment(new RetrievePaymentRequest(paymentId));
  }

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    public void onFragmentInteraction(Uri uri);
  }

  public void setStore(POSStore value){
    store = value;
  }

  private ICloverConnector getCloverConnector(){
    return cloverConnectorWeakReference.get();
  }

  public void setCloverConnector(ICloverConnector connector) {
    cloverConnectorWeakReference = new WeakReference<ICloverConnector>(connector);
  }
}
